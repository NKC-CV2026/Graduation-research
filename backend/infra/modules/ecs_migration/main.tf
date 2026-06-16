data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

data "aws_iam_policy_document" "task_db_connect" {
  statement {
    effect = "Allow"
    actions = [
      "rds-db:connect",
    ]
    resources = [
      "arn:aws:rds-db:${var.region}:${var.account_id}:dbuser:${var.db_cluster_resource_id}/${var.app_db_user}",
      "arn:aws:rds-db:${var.region}:${var.account_id}:dbuser:${var.db_cluster_resource_id}/${var.migration_db_user}",
    ]
  }
}

resource "aws_ecs_cluster" "this" {
  name = "${var.name_prefix}-cluster"

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-cluster"
  })
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/ecs/${var.name_prefix}-migration"
  retention_in_days = var.log_retention_in_days

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-migration-logs"
  })
}

resource "aws_iam_role" "execution" {
  name               = "${var.name_prefix}-ecs-execution"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-ecs-execution"
  })
}

resource "aws_iam_role_policy_attachment" "execution_managed" {
  role       = aws_iam_role.execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "task" {
  name               = "${var.name_prefix}-ecs-task"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-ecs-task"
  })
}

resource "aws_iam_role_policy" "task_db_connect" {
  name   = "${var.name_prefix}-ecs-db-connect"
  role   = aws_iam_role.task.id
  policy = data.aws_iam_policy_document.task_db_connect.json
}

locals {
  container_name = "migration"
}

resource "aws_ecs_task_definition" "this" {
  family                   = "${var.name_prefix}-migration"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = tostring(var.cpu)
  memory                   = tostring(var.memory)
  execution_role_arn       = aws_iam_role.execution.arn
  task_role_arn            = aws_iam_role.task.arn

  container_definitions = jsonencode([
    {
      name      = local.container_name
      image     = var.image_uri
      essential = true
      entryPoint = [
        "/migrate",
      ]
      environment = [
        for key, value in var.environment_variables : {
          name  = key
          value = value
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.this.name
          awslogs-region        = var.region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-migration-task"
  })
}

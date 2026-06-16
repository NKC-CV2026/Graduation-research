data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

data "aws_iam_policy_document" "db_connect" {
  statement {
    effect = "Allow"
    actions = [
      "rds-db:connect",
    ]
    resources = [
      "arn:aws:rds-db:${var.region}:${var.account_id}:dbuser:${var.db_cluster_resource_id}/${var.db_user}",
    ]
  }
}

resource "aws_iam_role" "this" {
  name               = "${var.name}-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json

  tags = merge(var.tags, {
    Name = "${var.name}-role"
  })
}

resource "aws_iam_role_policy_attachment" "basic" {
  role       = aws_iam_role.this.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "vpc_access" {
  role       = aws_iam_role.this.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy" "db_connect" {
  name   = "${var.name}-db-connect"
  role   = aws_iam_role.this.id
  policy = data.aws_iam_policy_document.db_connect.json
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/aws/lambda/${var.name}"
  retention_in_days = var.log_retention_in_days

  tags = merge(var.tags, {
    Name = "${var.name}-logs"
  })
}

resource "aws_lambda_function" "this" {
  function_name = var.name
  package_type  = "Image"
  image_uri     = var.image_uri
  role          = aws_iam_role.this.arn
  memory_size   = var.memory_size
  timeout       = var.timeout
  architectures = [var.architecture]

  environment {
    variables = var.environment_variables
  }

  vpc_config {
    subnet_ids         = var.subnet_ids
    security_group_ids = var.security_group_ids
  }

  depends_on = [aws_cloudwatch_log_group.this]

  tags = merge(var.tags, {
    Name = var.name
  })
}

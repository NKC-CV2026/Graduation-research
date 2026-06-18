resource "aws_db_subnet_group" "this" {
  name       = "${var.name_prefix}-db-subnet"
  subnet_ids = var.subnet_ids

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-db-subnet"
  })
}

resource "aws_security_group_rule" "postgres_ingress_from_app" {
  count = length(var.allowed_security_group_ids)

  type                     = "ingress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  source_security_group_id = var.allowed_security_group_ids[count.index]
  security_group_id        = var.vpc_security_group_ids[0]
}

resource "aws_rds_cluster" "this" {
  cluster_identifier                  = "${var.name_prefix}-aurora-pg"
  engine                              = "aurora-postgresql"
  engine_version                      = var.engine_version
  database_name                       = var.database_name
  master_username                     = var.master_username
  manage_master_user_password         = true
  db_subnet_group_name                = aws_db_subnet_group.this.name
  vpc_security_group_ids              = var.vpc_security_group_ids
  iam_database_authentication_enabled = true
  storage_encrypted                   = true
  backup_retention_period             = var.backup_retention_period
  deletion_protection                 = var.deletion_protection
  skip_final_snapshot                 = var.skip_final_snapshot
  enabled_cloudwatch_logs_exports     = ["postgresql"]
  copy_tags_to_snapshot               = true

  serverlessv2_scaling_configuration {
    min_capacity             = var.serverless_min_acu
    max_capacity             = var.serverless_max_acu
    seconds_until_auto_pause = var.seconds_until_auto_pause
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-aurora-pg"
  })
}

resource "aws_rds_cluster_instance" "writer" {
  identifier           = "${var.name_prefix}-aurora-pg-1"
  cluster_identifier   = aws_rds_cluster.this.id
  instance_class       = "db.serverless"
  engine               = aws_rds_cluster.this.engine
  engine_version       = aws_rds_cluster.this.engine_version
  publicly_accessible  = false
  db_subnet_group_name = aws_db_subnet_group.this.name

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-aurora-pg-1"
  })
}

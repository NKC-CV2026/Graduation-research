locals {
  endpoint_count = var.enable_vpc_endpoints ? 1 : 0

  interface_services = merge(
    {
      ecr_api = "com.amazonaws.${var.region}.ecr.api"
      ecr_dkr = "com.amazonaws.${var.region}.ecr.dkr"
      logs    = "com.amazonaws.${var.region}.logs"
    },
    var.enable_secretsmanager ? { secretsmanager = "com.amazonaws.${var.region}.secretsmanager" } : {},
    var.enable_kms ? { kms = "com.amazonaws.${var.region}.kms" } : {},
  )
}

resource "aws_security_group" "endpoints" {
  count = local.endpoint_count

  name        = "${var.name_prefix}-endpoints"
  description = "Security group for VPC interface endpoints"
  vpc_id      = var.vpc_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-endpoints"
  })
}

resource "aws_security_group_rule" "https_from_app" {
  count = local.endpoint_count * length(var.allowed_security_group_ids)

  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  source_security_group_id = var.allowed_security_group_ids[count.index]
  security_group_id        = aws_security_group.endpoints[0].id
}

resource "aws_vpc_endpoint" "interface" {
  for_each = var.enable_vpc_endpoints ? local.interface_services : {}

  vpc_id              = var.vpc_id
  service_name        = each.value
  vpc_endpoint_type   = "Interface"
  subnet_ids          = var.subnet_ids
  security_group_ids  = [aws_security_group.endpoints[0].id]
  private_dns_enabled = true

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-${each.key}"
  })
}

resource "aws_vpc_endpoint" "s3" {
  count = local.endpoint_count

  vpc_id            = var.vpc_id
  service_name      = "com.amazonaws.${var.region}.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids   = var.route_table_ids

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-s3"
  })
}

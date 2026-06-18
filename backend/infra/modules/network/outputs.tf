output "vpc_id" {
  description = "Created VPC ID."
  value       = aws_vpc.this.id
}

output "private_subnet_ids" {
  description = "Private subnet IDs."
  value       = aws_subnet.private[*].id
}

output "private_route_table_ids" {
  description = "Private route table IDs."
  value       = [aws_route_table.private.id]
}

output "db_security_group_id" {
  description = "Security group ID for the database."
  value       = aws_security_group.db.id
}

output "app_security_group_id" {
  description = "Security group ID for application workloads such as Lambda."
  value       = aws_security_group.app.id
}

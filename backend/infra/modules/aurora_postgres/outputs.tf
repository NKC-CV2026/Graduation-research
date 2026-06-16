output "cluster_arn" {
  description = "Aurora cluster ARN."
  value       = aws_rds_cluster.this.arn
}

output "cluster_id" {
  description = "Aurora cluster identifier."
  value       = aws_rds_cluster.this.cluster_identifier
}

output "cluster_resource_id" {
  description = "Aurora cluster resource ID used by IAM DB authentication."
  value       = aws_rds_cluster.this.cluster_resource_id
}

output "cluster_endpoint" {
  description = "Writer endpoint."
  value       = aws_rds_cluster.this.endpoint
}

output "reader_endpoint" {
  description = "Reader endpoint."
  value       = aws_rds_cluster.this.reader_endpoint
}

output "database_name" {
  description = "Initial database name."
  value       = aws_rds_cluster.this.database_name
}

output "master_user_secret_arn" {
  description = "Secrets Manager ARN for the managed master password."
  value       = aws_rds_cluster.this.master_user_secret[0].secret_arn
}

output "port" {
  description = "Database port."
  value       = aws_rds_cluster.this.port
}

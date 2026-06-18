variable "name_prefix" {
  description = "Prefix used for resource names."
  type        = string
}

variable "image_uri" {
  description = "Container image URI for the migration task."
  type        = string
}

variable "region" {
  description = "AWS region."
  type        = string
}

variable "account_id" {
  description = "AWS account ID."
  type        = string
}

variable "subnet_ids" {
  description = "Private subnet IDs for the migration task."
  type        = list(string)
}

variable "security_group_ids" {
  description = "Security group IDs for the migration task."
  type        = list(string)
}

variable "db_cluster_resource_id" {
  description = "Aurora cluster resource ID used for IAM DB authentication."
  type        = string
}

variable "app_db_user" {
  description = "Database username created for application and migration connectivity."
  type        = string
}

variable "db_secret_arn" {
  description = "Secrets Manager ARN for the master password secret used by the migration task."
  type        = string
}

variable "environment_variables" {
  description = "Environment variables passed to the migration container."
  type        = map(string)
  default     = {}
}

variable "cpu" {
  description = "Task CPU units."
  type        = number
  default     = 256
}

variable "memory" {
  description = "Task memory in MiB."
  type        = number
  default     = 512
}

variable "log_retention_in_days" {
  description = "CloudWatch Logs retention in days."
  type        = number
  default     = 7
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

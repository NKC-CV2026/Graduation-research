variable "name" {
  description = "Lambda function name."
  type        = string
}

variable "image_uri" {
  description = "Container image URI for Lambda."
  type        = string
}

variable "subnet_ids" {
  description = "Private subnet IDs for Lambda VPC attachment."
  type        = list(string)
}

variable "security_group_ids" {
  description = "Security group IDs for Lambda VPC attachment."
  type        = list(string)
}

variable "environment_variables" {
  description = "Environment variables for the Lambda function."
  type        = map(string)
  default     = {}
}

variable "memory_size" {
  description = "Lambda memory size in MB."
  type        = number
  default     = 256
}

variable "timeout" {
  description = "Lambda timeout in seconds."
  type        = number
  default     = 15
}

variable "architecture" {
  description = "Lambda architecture."
  type        = string
  default     = "x86_64"
}

variable "log_retention_in_days" {
  description = "CloudWatch Logs retention in days."
  type        = number
  default     = 7
}

variable "region" {
  description = "AWS region."
  type        = string
}

variable "account_id" {
  description = "AWS account ID."
  type        = string
}

variable "db_cluster_resource_id" {
  description = "Aurora cluster resource ID used for IAM DB authentication."
  type        = string
}

variable "db_user" {
  description = "Database username used by the Lambda runtime."
  type        = string
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

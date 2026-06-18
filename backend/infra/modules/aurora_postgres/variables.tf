variable "name_prefix" {
  description = "Prefix used for resource names."
  type        = string
}

variable "database_name" {
  description = "Initial database name."
  type        = string
}

variable "master_username" {
  description = "Master username for the cluster."
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs used by the DB subnet group."
  type        = list(string)
}

variable "vpc_security_group_ids" {
  description = "Security groups attached to the cluster."
  type        = list(string)
}

variable "allowed_security_group_ids" {
  description = "Security group IDs allowed to connect to PostgreSQL."
  type        = list(string)
  default     = []
}

variable "engine_version" {
  description = "Aurora PostgreSQL engine version."
  type        = string
  default     = "16.6"
}

variable "serverless_min_acu" {
  description = "Minimum ACU for Aurora Serverless v2. Use 0 only when supported by the engine version in the target region."
  type        = number
  default     = 0
}

variable "serverless_max_acu" {
  description = "Maximum ACU for Aurora Serverless v2."
  type        = number
  default     = 2
}

variable "seconds_until_auto_pause" {
  description = "Idle seconds before Aurora Serverless v2 auto-pauses."
  type        = number
  default     = 900
}

variable "backup_retention_period" {
  description = "Automated backup retention in days."
  type        = number
  default     = 1
}

variable "deletion_protection" {
  description = "Enable deletion protection."
  type        = bool
  default     = false
}

variable "skip_final_snapshot" {
  description = "Skip final snapshot on deletion."
  type        = bool
  default     = true
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

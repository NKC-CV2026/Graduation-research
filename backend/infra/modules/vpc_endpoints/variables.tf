variable "name_prefix" {
  description = "Prefix used for resource names."
  type        = string
}

variable "vpc_id" {
  description = "Target VPC ID."
  type        = string
}

variable "region" {
  description = "AWS region for endpoints."
  type        = string
}

variable "subnet_ids" {
  description = "Subnet IDs used for interface endpoints."
  type        = list(string)
}

variable "route_table_ids" {
  description = "Route table IDs used for gateway endpoints."
  type        = list(string)
}

variable "allowed_security_group_ids" {
  description = "Security groups allowed to reach interface endpoints over HTTPS."
  type        = list(string)
}

variable "enable_secretsmanager" {
  description = "Whether to create a Secrets Manager interface endpoint."
  type        = bool
  default     = false
}

variable "enable_kms" {
  description = "Whether to create a KMS interface endpoint."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

variable "enable_vpc_endpoints" {
  description = "Whether to create VPC endpoints."
  type        = bool
  default     = false
}

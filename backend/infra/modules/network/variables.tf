variable "name_prefix" {
  description = "Prefix used for resource names."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
}

variable "availability_zones" {
  description = "Availability zones used for the subnets."
  type        = list(string)
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets. Must align with availability_zones."
  type        = list(string)
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

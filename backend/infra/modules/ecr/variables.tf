variable "name" {
  description = "ECR repository name."
  type        = string
}

variable "tags" {
  description = "Common tags applied to resources."
  type        = map(string)
  default     = {}
}

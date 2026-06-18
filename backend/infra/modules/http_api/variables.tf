variable "name" {
  description = "HTTP API name."
  type        = string
}

variable "lambda_function_name" {
  description = "Lambda function name used by the API integration."
  type        = string
}

variable "lambda_invoke_arn" {
  description = "Lambda invoke ARN used by the API integration."
  type        = string
}

variable "cors_allowed_origins" {
  description = "Allowed origins for CORS."
  type        = list(string)
  default     = ["*"]
}

variable "cors_allowed_methods" {
  description = "Allowed methods for CORS."
  type        = list(string)
  default     = ["GET"]
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

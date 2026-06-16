variable "aws_region" {
  description = "AWS region used for bootstrap resources."
  type        = string
  default     = "ap-northeast-3"
}

variable "aws_profile" {
  description = "Optional shared AWS profile name for local Terraform execution."
  type        = string
  default     = null
}

variable "github_org" {
  description = "GitHub organization or user name."
  type        = string
  default     = "NKC-CV2026"
}

variable "github_repo" {
  description = "GitHub repository name."
  type        = string
  default     = "Graduation-research"
}

variable "github_default_branch" {
  description = "Default branch used by apply workflow."
  type        = string
  default     = "main"
}

variable "terraform_state_bucket_prefix" {
  description = "Prefix used for the Terraform state bucket name. A random suffix is appended automatically."
  type        = string
  default     = "gr9-terraform-state"
}

variable "terraform_state_bucket_suffix" {
  description = "Optional fixed suffix for the Terraform state bucket name. If null, Terraform generates one on the first apply."
  type        = string
  default     = null
}

variable "terraform_state_bucket_force_destroy" {
  description = "Allow destroying the state bucket even when it contains objects."
  type        = bool
  default     = false
}

variable "backend_app_ecr_repository_name" {
  description = "ECR repository name used for the backend application image."
  type        = string
  default     = "gr9-backend-app"
}

variable "tags" {
  description = "Common tags applied to bootstrap resources."
  type        = map(string)
  default = {
    Project    = "gr9"
    ManagedBy  = "Terraform"
    Scope      = "bootstrap"
    Repository = "NKC-CV2026/Graduation-research"
  }
}

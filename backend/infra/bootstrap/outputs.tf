output "aws_region" {
  description = "AWS region used for bootstrap resources."
  value       = var.aws_region
}

output "terraform_state_bucket_name" {
  description = "Generated S3 bucket name for Terraform state."
  value       = aws_s3_bucket.terraform_state.bucket
}

output "terraform_state_bucket_suffix" {
  description = "Bucket suffix to pin in tfvars after the first bootstrap apply if you want to stop generating it dynamically."
  value       = local.bucket_suffix
}

output "terraform_plan_role_arn" {
  description = "IAM role ARN for GitHub Actions plan workflow."
  value       = aws_iam_role.terraform_plan.arn
}

output "terraform_apply_role_arn" {
  description = "IAM role ARN for GitHub Actions apply workflow."
  value       = aws_iam_role.terraform_apply.arn
}

output "github_repository" {
  description = "GitHub repository allowed to assume the OIDC roles."
  value       = local.repository
}

output "bootstrap_backend_config_hint" {
  description = "Example backend settings to use when migrating bootstrap state to S3."
  value = {
    bucket = aws_s3_bucket.terraform_state.bucket
    region = var.aws_region
    key    = "bootstrap/terraform.tfstate"
  }
}

output "backend_app_ecr_repository_name" {
  description = "ECR repository name for the backend application image."
  value       = aws_ecr_repository.backend_app.name
}

output "backend_app_ecr_repository_url" {
  description = "ECR repository URL for the backend application image."
  value       = aws_ecr_repository.backend_app.repository_url
}

locals {
  repository        = "${var.github_org}/${var.github_repo}"
  oidc_provider_url = "https://token.actions.githubusercontent.com"
  oidc_client_ids   = ["sts.amazonaws.com"]
  plan_role_name    = "gr9-terraform-plan"
  apply_role_name   = "gr9-terraform-apply"
  state_policy_name = "gr9-terraform-state-access"
  plan_subjects     = ["repo:${local.repository}:pull_request"]
  apply_subjects    = ["repo:${local.repository}:ref:refs/heads/${var.github_default_branch}"]
  bucket_suffix     = coalesce(var.terraform_state_bucket_suffix, random_id.bucket_suffix.hex)
}

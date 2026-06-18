terraform {
  backend "s3" {
    bucket="gr9-terraform-state-afbf602e"
    key="env/dev/terraform.tfstate"
    region="ap-northeast-3"
    use_lockfile=true
  }
}

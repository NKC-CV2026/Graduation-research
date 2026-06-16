terraform {
  backend "s3" {
    bucket = "gr9-terraform-state-afbf602e"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-3"
  }
}

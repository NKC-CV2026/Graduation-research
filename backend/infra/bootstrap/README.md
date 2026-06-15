# Terraform Bootstrap

`backend/infra/bootstrap` は Terraform の state bucket と GitHub Actions 用 OIDC IAM Role を最初に作るための bootstrap 構成です。

## 作成するもの

- Terraform state 用 S3 bucket
- GitHub Actions OIDC provider
- PR 用 `terraform-plan` IAM role
- `main` push 用 `terraform-apply` IAM role

## 前提

- 初回は local state で実行します
- AWS region は大阪リージョン `ap-northeast-3` をデフォルトにしています
- ローカル実行時は AWS 認証情報が必要です。必要なら `aws_profile` を指定します
- GitHub repository は `NKC-CV2026/Graduation-research` をデフォルトにしています
- 命名は `gr9-<用途>[-<suffix>]` を基本にしています
- state bucket 名は `gr9-terraform-state-<suffix>` 形式です
- suffix は未指定時に初回 apply で 16 進数文字列を生成し、後から `terraform_state_bucket_suffix` に固定値として転記できます
- `plan` role は `repo:NKC-CV2026/Graduation-research:pull_request` のみ許可します
- `apply` role は `repo:NKC-CV2026/Graduation-research:ref:refs/heads/main` のみ許可します

## 初回実行

```bash
cd backend/infra/bootstrap
cp terraform.tfvars.example terraform.tfvars
terraform init -backend=false
terraform plan
terraform apply
```

共有 profile を使う場合の例:

```hcl
aws_profile = "your-profile"
```

apply 後に以下を控えてください。

- `terraform_state_bucket_name`
- `terraform_state_bucket_suffix`
- `terraform_plan_role_arn`
- `terraform_apply_role_arn`

その後、bucket suffix を固定したい場合は `terraform.tfvars` に次を追記します。

```hcl
terraform_state_bucket_suffix = "<terraform_state_bucket_suffix output>"
```

## bootstrap state を S3 に移行する手順

`backend.tf` では bootstrap の現在値を使って S3 backend を宣言しています。

```hcl
terraform {
  backend "s3" {
    bucket = "gr9-terraform-state-afbf602e"
    key    = "bootstrap/terraform.tfstate"
    region = "ap-northeast-3"
  }
}
```

1. 必要なら local state をバックアップします
2. 次のように `terraform init -migrate-state` を実行します

```bash
terraform init -migrate-state
```

共有 profile を使う場合の例:

```bash
AWS_PROFILE=<your-profile> terraform init -migrate-state
```

移行後は次を確認します。

- `terraform plan` が `No changes` になること
- S3 に `bootstrap/terraform.tfstate` が作成されていること

## GitHub Actions で使う Variables

Repository Variables に次を設定します。

- `AWS_REGION`: `ap-northeast-3`
- `TERRAFORM_BOOTSTRAP_PLAN_ROLE_ARN`: bootstrap output の plan role ARN
- `TERRAFORM_BOOTSTRAP_APPLY_ROLE_ARN`: bootstrap output の apply role ARN
- `TERRAFORM_BOOTSTRAP_READY`: `true`

`TERRAFORM_BOOTSTRAP_READY` は bootstrap state の S3 移行後に設定してください。local state のまま CI から apply しないためのガードです。

## IAM の考え方

- plan role: `ReadOnlyAccess` + state bucket access
- apply role: `AdministratorAccess` + state bucket access

apply role は広い権限を持つので、後続の `env/dev` `env/prod` の実装が見えた段階で絞り込む前提です。

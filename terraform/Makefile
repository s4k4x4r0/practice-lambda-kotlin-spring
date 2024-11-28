.PHONY: help init plan apply destroy fmt validate output state-list state-show

help:
	@echo "使用可能なコマンド:"
	@echo "  make init          - Terraformの初期化"
	@echo "  make plan         - 実行計画の確認"
	@echo "  make apply        - インフラの適用"
	@echo "  make destroy      - インフラの削除"
	@echo "  make fmt          - コードのフォーマット"
	@echo "  make validate     - 構成の検証"
	@echo "  make output       - 出力値の表示"
	@echo "  make state-list   - 管理リソースの一覧表示"
	@echo "  make state-show   - 指定リソースの詳細表示（例: make state-show address=aws_s3_bucket.backend_jar）"

# AWS環境用のコマンド
init:
	source .envrc.local && cd ./dev && ../helper/terraform-init.sh

plan:
	source .envrc.local && cd ./dev && terraform plan

apply:
	source .envrc.local && cd ./dev && terraform apply

fmt:
	cd ./dev && terraform fmt -recursive

validate: init
	source .envrc.local && cd ./dev && terraform validate

output:
	source .envrc.local && cd ./dev && terraform output

# 現在のstateで管理されているリソースの一覧を表示
# 使用例: make state-list
state-list:
	source .envrc.local && cd ./dev && terraform state list

# 指定したリソースの詳細な状態を表示
# 使用例: 
#   make state-show address=aws_s3_bucket.backend_jar
#   make state-show address=aws_iam_policy.uppercase
#   make state-show address=module.hello.aws_lambda_function.this
state-show:
	@if [ -z "$(address)" ]; then \
		echo "Error: address parameter is required"; \
		echo "Usage: make state-show address=<resource_address>"; \
		exit 1; \
	fi
	source .envrc.local && cd ./dev && terraform state show $(address)
	
#!/bin/bash

set -eo pipefail

# 必要な環境変数のチェック
required_vars=("TFSTATE_BUCKET" "AWS_REGION")
for var in "${required_vars[@]}"; do  
    if [ -z "${!var}" ]; then
        echo "Error: Environment variable $var is not set."
        exit 1
    fi
done

# tfenvのバージョン確認と設定
if ! command -v tfenv &> /dev/null; then
    echo "Error: tfenv is not installed."
    exit 1
fi

tfenv use || {
    echo "Error: Failed to set Terraform version using tfenv"
    exit 1
}

# バックエンドの設定ファイルが存在するか確認
if [ ! -f "backend.tf" ]; then
    echo "Warning: backend.tf file not found"
fi

# terraform initの実行
echo "Initializing Terraform with S3 backend in ${AWS_REGION}..."
terraform init -migrate-state \
    -backend-config="bucket=${TFSTATE_BUCKET}" \
    -backend-config="key=practice-lambda-kotlin-spring.tfstate" \
    -backend-config="region=${AWS_REGION}"

echo "Terraform initialization completed successfully"
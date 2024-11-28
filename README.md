## 環境設定

### 必要な環境ファイル

プロジェクトを実行する前に、`.envrc.local`ファイルをプロジェクトのルートディレクトリに作成する必要があります。

以下の環境変数を設定してください：

```bash: .envrc.local
#AWSの認証情報
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_DEFAULT_REGION="ap-northeast-1"
# Terraformのバックエンド用S3バケット
export TF_VAR_backend_bucket="your-bucket"

```

これらの設定は以下の目的で使用されます：

1. AWS 認証情報

   - Lambda 関数のデプロイ
   - S3 バケットへのアクセス
   - API Gateway の設定

2. Terraform バックエンド設定
   - Terraform の状態管理用 S3 バケット名の指定

注意：`.envrc.local`ファイルは`.gitignore`に含まれており、Git 管理対象外です。セキュリティのため、このファイルを Git にコミットしないでください。

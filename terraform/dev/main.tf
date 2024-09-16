resource "aws_api_gateway_rest_api" "api" {
  name        = var.project
  description = "Example API with multiple Lambda functions"
  body = templatefile("${path.root}/../../openapi/openapi.yaml", {
    region    = data.aws_region.current.name
    accountId = data.aws_caller_identity.self.account_id
  })
}

module "api_handlers" {
  for_each = {
    hello = {
      iam_policy_arns = [aws_iam_policy.hello.arn]
    }
    uppercase = {
      iam_policy_arns = [aws_iam_policy.uppercase.arn]
      memory_size     = 256
  } }

  source = "../modules/api_handler"

  jar_file_path     = reverse(sort(tolist(fileset("", "${path.root}/../../backend/build/libs/backend-*-aws.jar"))))[0]
  api_execution_arn = aws_api_gateway_rest_api.api.execution_arn
  operation_id      = each.key
  function_settings = each.value
}

resource "aws_iam_policy" "hello" {
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Sid      = "HelloSamplePolicy"
      Action   = "logs:*",
      Effect   = "Allow",
      Resource = "*"
      }
      # 個別の権限を設定
    ]
  })
}

resource "aws_iam_policy" "uppercase" {
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Sid      = "UppercaseSamplePolicy"
      Action   = "logs:*",
      Effect   = "Allow",
      Resource = "*"
      }
      # 個別の権限を設定
    ]
  })
}

resource "aws_s3_bucket" "backend_jar" {
  bucket_prefix = "backend-jar"
  force_destroy = true
}

resource "aws_s3_bucket_lifecycle_configuration" "backend_jar" {
  bucket = aws_s3_bucket.backend_jar.id

  rule {
    id     = "expiration after a certain period"
    status = "Enabled"

    expiration {
      days = 7
    }

  }
}

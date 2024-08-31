# resource "aws_api_gateway_rest_api" "example_api" {
#   name        = "example-api"
#   description = "API with multiple Lambda functions"
#   body        = file("${path.module}/openapi/openapi.yaml")
# }

locals {
  jar_file_path = "${path.module}/../backend/build/libs/backend-0.1-aws.jar"
}

resource "aws_lambda_function" "hello" {
  function_name    = "hello"
  runtime          = "java21"
  handler          = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  role             = aws_iam_role.lambda_exec.arn
  filename         = local.jar_file_path
  source_code_hash = filesha256(local.jar_file_path)
  timeout          = 29
}

resource "aws_lambda_function" "uppercase" {
  function_name    = "uppercase"
  runtime          = "java21"
  handler          = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  role             = aws_iam_role.lambda_exec.arn
  filename         = local.jar_file_path
  source_code_hash = filesha256(local.jar_file_path)
  timeout          = 29
}

resource "aws_iam_role" "lambda_exec" {
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy" "lambda_exec_policy" {
  role = aws_iam_role.lambda_exec.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action   = "logs:*",
      Effect   = "Allow",
      Resource = "*"
    }]
  })
}

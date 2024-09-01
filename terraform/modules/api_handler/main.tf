resource "aws_lambda_function" "api_handler" {
  function_name    = var.operation_id
  runtime          = "java21"
  handler          = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  role             = aws_iam_role.lambda.arn
  filename         = var.jar_file_path
  source_code_hash = filesha256(var.jar_file_path)
  timeout          = 29
  memory_size      = var.function_settings.memory_size

  environment {
    variables = {
      spring_cloud_function_definition = "${var.operation_id}Function"
    }
  }
}

resource "aws_lambda_permission" "api_handler" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.api_handler.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${var.api_execution_arn}/*/*/${var.operation_id}"
}

resource "aws_iam_role" "lambda" {
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

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "lambda" {
  for_each = toset(var.function_settings.iam_policy_arns)

  role       = aws_iam_role.lambda.name
  policy_arn = each.key
}

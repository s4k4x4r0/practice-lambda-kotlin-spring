variable "operation_id" {
  description = "The unique identifier for the Lambda function, used to set the function name and other related resources."
  type        = string
}

variable "jar_file" {
  description = "S3バケットとキーの情報を含むJARファイルの場所"
  type = object({
    bucket = string
    key    = string
  })
}

variable "function_settings" {
  description = "The settings applied to Lambda functions"
  type = object({
    iam_policy_arns = optional(list(string), [])
    memory_size     = optional(string)
  })
}

variable "api_execution_arn" {
  description = "The ARN of the API Gateway execution stage that will invoke the Lambda function."
  type        = string
}

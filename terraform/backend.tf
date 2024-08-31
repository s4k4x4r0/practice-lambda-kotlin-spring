provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      Project = "practice-lambda-kotlin-spring"
    }
  }
}

terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }

  backend "s3" {
    bucket = "your-bucket"
    key    = "practice-lambda-kotlin-spring.tfstate"
  }
}


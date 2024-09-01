provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      Project = var.project
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


terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
  alias  = "default"
}

provider "aws" {
  alias                       = "localstack"
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    dynamodb = "http://localhost:4566"
    sqs      = "http://localhost:4566"
  }
}

module "dynamodb_local" {
  source      = "./modules/dynamodb"
  table_name  = var.dynamodb_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
}

module "dynamodb_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.dynamodb_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
}

module "sqs_local" {
  source      = "./modules/sqs"
  queue_name  = var.sqs_queue_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
}

module "sqs_aws" {
  source      = "./modules/sqs"
  queue_name  = var.sqs_queue_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
}

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

module "activations_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_activations_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0

  hash_key   = "macAddress"
  attributes = [
    {
      name = "macAddress"
      type = "S"
    }
  ]
}

module "activations_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_activations_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1

  hash_key   = "macAddress"
  attributes = [
    {
      name = "macAddress"
      type = "S"
    }
  ]
}

module "models_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_models_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0

  hash_key   = "model"
  attributes = [
    {
      name = "model"
      type = "S"
    }
  ]

  tags = {
    Project = "Zooby"
  }

}

module "models_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_models_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1

  hash_key   = "model"
  attributes = [
    {
      name = "model"
      type = "S"
    }
  ]

  tags = {
    Project = "Zooby"
  }


}

module "inventory_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_inventory_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0

  hash_key   = "serial_number"
  attributes = [
    {
      name = "serial_number"
      type = "S"
    }
  ]

  tags = {
    Project = "Zooby"
  }
}

module "inventory_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_inventory_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1

  hash_key   = "serial_number"
  attributes = [
    {
      name = "serial_number"
      type = "S"
    }
  ]

  tags = {
    Project = "Zooby"
  }
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

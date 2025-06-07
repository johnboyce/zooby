terraform {
  required_version = ">= 1.6.0, < 2.0.0"
  backend "s3" {
    # Leave empty or just use `partial_configuration = true`
  }
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

locals {
  common_tags = {
    Project     = "zooby"
    Environment = var.environment
  }
}

module "activations_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_activations_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
  hash_key    = "macAddress"
  attributes = [
    {
      name = "macAddress"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "activations_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_activations_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
  hash_key    = "macAddress"
  attributes = [
    {
      name = "macAddress"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "models_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_models_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
  hash_key    = "model"
  attributes = [
    {
      name = "model"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "models_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_models_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
  hash_key    = "model"
  attributes = [
    {
      name = "model"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "inventory_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_inventory_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
  hash_key    = "serial_number"
  attributes = [
    {
      name = "serial_number"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "inventory_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_inventory_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
  hash_key    = "serial_number"
  attributes = [
    {
      name = "serial_number"
      type = "S"
    }
  ]
  tags = local.common_tags
}

module "frontend_ecr" {
  source      = "./modules/ecr"
  repo_name   = "zooby-frontend"
  environment = var.environment
}

module "github_oidc" {
  source = "./modules/github_oidc"
  repo_name  = "zooby"
  repo_owner = "johnboyce"
  role_name  = "zooby-github-actions-role"
}

module "apprunner_qa" {
  source = "./modules/apprunner"

  service_name     = var.frontend_service_name
  image_identifier = "020157571320.dkr.ecr.${var.aws_region}.amazonaws.com/zooby-frontend:latest"
  environment      = var.environment
  aws_region       = var.aws_region

  port   = "3000"
  cpu    = "1024"
  memory = "2048"

  env_vars = {
    NODE_ENV        = "production"
    NEXTAUTH_URL    = var.nextauth_url
    NEXTAUTH_SECRET = "john"
    OAUTH_CLIENT_SECRET = var.oauth_client_secret# 🔐 Use Terraform external ref if needed
  }
}

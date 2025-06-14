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

module "vpc" {
  source               = "./modules/vpc"
  name                 = "zooby-qa"
  vpc_cidr             = "10.0.0.0/16"
  public_subnet_cidrs  = ["10.0.1.0/24", "10.0.2.0/24"]
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

module "users_table_aws" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_users_table_name
  environment = var.environment
  providers   = { aws = aws.default }
  count       = var.use_localstack ? 0 : 1
  hash_key    = "user_id"
  attributes = [
    {
      name = "user_id"
      type = "S"
    },
    {
      name = "provider"
      type = "S"
    },
    {
      name = "provider_id"
      type = "S"
    }
  ]
  global_secondary_indexes = [
    {
      name            = "provider-provider_id-index"
      hash_key        = "provider"
      range_key       = "provider_id"
      projection_type = "ALL"
    }
  ]
  tags = local.common_tags
}

module "users_table_local" {
  source      = "./modules/dynamodb"
  table_name  = var.zooby_users_table_name
  environment = var.environment
  providers   = { aws = aws.localstack }
  count       = var.use_localstack ? 1 : 0
  hash_key    = "user_id"
  attributes = [
    {
      name = "user_id"
      type = "S"
    },
    {
      name = "provider"
      type = "S"
    },
    {
      name = "provider_id"
      type = "S"
    }
  ]
  global_secondary_indexes = [
    {
      name            = "provider-provider_id-index"
      hash_key        = "provider"
      range_key       = "provider_id"
      projection_type = "ALL"
    }
  ]
  tags = local.common_tags
}

module "frontend_ecr" {
  source      = "./modules/ecr"
  repo_name   = "zooby-frontend"
  environment = var.environment
}

module "backend_ecr" {
  source      = "./modules/ecr"
  repo_name   = "zooby-backend"
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
  cpu    = "256"   # 0.25 vCPU
  memory = "512"   # 0.5 GB

  env_vars = {
    NODE_ENV        = "production"
    NEXTAUTH_URL    = var.nextauth_url
    NEXTAUTH_SECRET = "john"
    OAUTH_CLIENT_SECRET = var.oauth_client_secret# 🔐 Use Terraform external ref if needed
  }
}

module "ecs_cluster" {
  source = "./modules/ecs_cluster"
  name   = var.cluster_name
  vpc_id = module.vpc.vpc_id

}

module "zooby_backend" {
  source = "./modules/ecs_service"

  name               = "zooby-backend"
  family             = "zooby-backend-task"
  cpu                   = var.cpu
  memory                = var.memory
  environment_variables = var.environment_variables
  cluster_arn        = module.ecs_cluster.arn
  execution_role_arn = module.ecs_iam.execution_role_arn
  task_role_arn      = module.ecs_iam.task_role_arn
  target_group_arn   = module.alb.zooby_backend_target_group_arn

  image_url          = "020157571320.dkr.ecr.us-east-1.amazonaws.com/zooby-backend:qa"
  app_container_name = "zooby-backend"
  container_port     = 8080

  subnet_ids         = module.vpc.public_subnet_ids
  security_group_ids  = [module.alb.security_group_id, module.ecs_cluster.fargate_sg_id]
  aws_region         = var.aws_region

  desired_count      = 1
  include_sidecar    = true

}


module "ecs_iam" {
  source       = "./modules/ecs_iam"
  name_prefix  = "zooby-backend"

  task_permissions = [
    {
      Effect   = "Allow"
      Action   = ["dynamodb:GetItem", "dynamodb:PutItem"]
      Resource = ["arn:aws:dynamodb:us-east-1:020157571320:table/zooby-*"]
    },
    {
      Effect   = "Allow"
      Action   = ["sqs:SendMessage"]
      Resource = ["arn:aws:sqs:us-east-1:020157571320:zooby-*"]
    }
  ]
}

module "alb" {
  source = "./modules/alb"

  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = module.vpc.public_subnet_ids
}

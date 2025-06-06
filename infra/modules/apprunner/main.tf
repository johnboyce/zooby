resource "aws_apprunner_connection" "github" {
  connection_name = var.connection_name
  provider_type   = "GITHUB"
}

resource "aws_apprunner_service" "frontend" {
  service_name = var.service_name

  source_configuration {
    authentication_configuration {
      connection_arn = aws_apprunner_connection.github.arn
    }

    code_repository {
      repository_url = var.repository_url

      source_code_version {
        type  = "BRANCH"
        value = var.branch
      }

      source_directory = var.source_directory  # ✅ ADD THIS LINE

      code_configuration {
        configuration_source = "API"
        code_configuration_values {
          runtime       = "NODEJS_16"
          build_command = "npm install && npm run build"
          start_command = "npm run start"
          port          = "3000"
          runtime_environment_variables = {
            NODE_ENV = "production"
          }
        }
      }
    }


    auto_deployments_enabled = true
  }

  instance_configuration {
    cpu    = "1024"
    memory = "2048"
  }

  tags = {
    Environment = var.environment
  }
}

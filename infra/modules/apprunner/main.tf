resource "aws_iam_role" "apprunner_ecr_access" {
  name = "${var.service_name}-access-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = {
        Service = "build.apprunner.amazonaws.com"
      },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecr_read" {
  role       = aws_iam_role.apprunner_ecr_access.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}


resource "aws_apprunner_service" "frontend" {
  service_name = var.service_name

  source_configuration {
    image_repository {
      image_identifier      = var.image_identifier
      image_repository_type = "ECR"
      image_configuration {
        port = var.port

        runtime_environment_variables = var.env_vars
      }
    }

    authentication_configuration {
      access_role_arn = aws_iam_role.apprunner_ecr_access.arn
    }

    auto_deployments_enabled = false
  }

  instance_configuration {
    cpu    = var.cpu
    memory = var.memory
  }

  auto_scaling_configuration_arn = aws_apprunner_auto_scaling_configuration_version.this.arn

  tags = {
    Environment = var.environment
  }
}

resource "aws_ecs_task_definition" "this" {
  family                   = var.family
  requires_compatibilities = ["FARGATE"]
  network_mode            = "awsvpc"
  cpu                     = var.cpu
  memory                  = var.memory
  execution_role_arn      = var.execution_role_arn
  task_role_arn           = var.task_role_arn

  container_definitions = jsonencode([
    {
      name      = var.app_container_name
      image     = var.image_url
      portMappings = [
        {
          containerPort = var.container_port
          protocol      = "tcp"
        }
      ],
      environment = var.environment_variables,
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = var.log_group
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = var.app_container_name
        }
      }
    },
      var.include_sidecar ? {
      name      = "otel-sidecar"
      image     = var.sidecar_image
      essential = false
    } : null
  ])
}

resource "aws_cloudwatch_log_group" "this" {
  name              = var.log_group
  retention_in_days = 7
}

resource "aws_ecs_service" "this" {
  name            = var.name
  cluster         = var.cluster_arn
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = var.desired_count
  launch_type     = null  # <-- disable default FARGATE launch type

  network_configuration {
    subnets          = var.subnet_ids
    security_groups  = var.security_group_ids
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = var.app_container_name
    container_port   = var.container_port
  }

  capacity_provider_strategy {
    capacity_provider = "FARGATE_SPOT"
    weight            = 1
  }

  depends_on = [aws_ecs_task_definition.this]
}


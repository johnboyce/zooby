output "name" {
  value = aws_ecs_cluster.this.name
}

output "arn" {
  value = aws_ecs_cluster.this.arn
}

output "fargate_sg_id" {
  value = aws_security_group.fargate.id
}

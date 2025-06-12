output "alb_dns_name" {
  value = aws_lb.zooby.dns_name
}

output "zooby_backend_target_group_arn" {
  value = aws_lb_target_group.zooby_backend.arn
}

output "security_group_id" {
  value = aws_security_group.alb.id
}

output "alb_health_check_url" {
  value = "http://${aws_lb.zooby.dns_name}/q/health"
}

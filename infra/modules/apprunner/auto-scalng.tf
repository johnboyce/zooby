resource "aws_apprunner_auto_scaling_configuration_version" "this" {
  auto_scaling_configuration_name = "${var.service_name}-scaling"
  max_concurrency                 = 20
  max_size                        = 1
  min_size                        = 1
}

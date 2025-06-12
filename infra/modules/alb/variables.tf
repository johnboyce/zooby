variable "vpc_id" {
  type        = string
  description = "VPC ID to launch the ALB into"
}

variable "public_subnet_ids" {
  type        = list(string)
  description = "Subnets for ALB"
}

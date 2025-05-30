variable "environment" { type = string }
variable "aws_region" { type = string }

variable "zooby_activations_table_name" { type = string }
variable "zooby_models_table_name" { type = string }
variable "zooby_inventory_table_name" { type = string }

variable "sqs_queue_name" { type = string }
variable "use_localstack" { type = bool }

environment         = "qa"
aws_region          = "us-east-1"

sqs_queue_name      = "zooby-qa-queue"
use_localstack      = false

zooby_activations_table_name = "zooby-qa-activations"
zooby_models_table_name      = "zooby-qa-models"
zooby_inventory_table_name   = "zooby-qa-inventory"


frontend_service_name    = "zooby-frontend-qa"
frontend_repo_url        = "https://github.com/YOUR-ORG/zooby-frontend"
frontend_branch          = "qa"
frontend_connection_arn  = "arn:aws:apprunner:us-east-1:123456789012:connection/github-connection-id"

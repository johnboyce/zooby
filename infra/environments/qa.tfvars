environment = "qa"
aws_region  = "us-east-1"

sqs_queue_name = "zooby-qa-queue"
use_localstack = false

zooby_activations_table_name = "zooby-qa-activations"
zooby_models_table_name      = "zooby-qa-models"
zooby_inventory_table_name   = "zooby-qa-inventory"

frontend_service_name = "zooby-frontend-qa"

nextauth_url        = "https://7qdnizqzpi.us-east-1.awsapprunner.com"
oauth_client_secret = "tgpY40U6Ozuf804U/g8oMQORh4fPDWdRiPXN2hLrSKw="

cluster_name    = "zooby-qa-cluster"
cpu             = "512"
memory          = "1024"
quarkus_profile = "qa"

environment_variables = [
  { name = "STAGE", value = "qa" },
  { name = "QUARKUS_PROFILE", value = "qa" },
  { name = "LOG_LEVEL", value = "debug" }
]

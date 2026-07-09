environment    = "local"
aws_region     = "us-east-1"
sqs_queue_name = "zooby-local-queue"
use_localstack = true

zooby_activations_table_name = "zooby-local-activations"
zooby_models_table_name      = "zooby-local-models"
zooby_inventory_table_name   = "zooby-local-inventory"
zooby_users_table_name       = "zooby-local-users"

frontend_service_name = "zooby-frontend-local"

# Local development URLs - not used with LocalStack but required by variables
nextauth_url        = "http://localhost:3000"
oauth_client_secret = "local-dev-secret"

cluster_name    = "zooby-local-cluster"
cpu             = "256"
memory          = "512"
quarkus_profile = "dev"

environment_variables = [
  { name = "STAGE", value = "local" },
  { name = "QUARKUS_PROFILE", value = "dev" },
  { name = "LOG_LEVEL", value = "debug" }
]

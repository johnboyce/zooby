  #!/usr/bin/env python3

import os
import json
import boto3
from botocore.config import Config

dynamodb = boto3.resource(
    'dynamodb',
    region_name='us-east-1',
    endpoint_url=os.getenv('DYNAMODB_ENDPOINT', None),
    aws_access_key_id='test' if 'DYNAMODB_ENDPOINT' in os.environ else None,
    aws_secret_access_key='test' if 'DYNAMODB_ENDPOINT' in os.environ else None,
    config=Config(retries={'max_attempts': 10, 'mode': 'standard'})
)

def seed_models():
    table = dynamodb.Table('zooby-local-models')
    with open('../src/main/resources/zooby_models.json') as f:
        models = json.load(f)
    for model in models:
        table.put_item(Item=model)
    print(f"Seeded {len(models)} models.")

def seed_inventory():
    table = dynamodb.Table('zooby-local-inventory')
    with open('../src/main/resources/zooby_inventory.json') as f:
        inventory = json.load(f)
    for item in inventory:
        table.put_item(Item=item)
    print(f"Seeded {len(inventory)} inventory items.")

if __name__ == '__main__':
    seed_models()
    seed_inventory()

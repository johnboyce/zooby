package com.zooby.data;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Startup
@ApplicationScoped
public class DynamoDbModelSeeder {

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "zooby.models.table")
    String modelsTableName;

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    @PostConstruct
    void seedData() {
        try {
            TableData tableData = loadJsonResource("/schema/models.json", TableData.class);

            if (!activeProfile.equals("prod")) {
                createTableIfNotExists(tableData);
                waitForTableActive();
            }

            if (tableData.getItems() != null) {
                tableData.getItems().forEach(item -> {
                    try {
                        PutItemRequest request = PutItemRequest.builder()
                            .tableName(modelsTableName)
                            .item(convertToAttributeMap(item))
                            .build();
                        dynamoDb.putItem(request);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to insert item: " + item.getModel(), e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed DynamoDB data", e);
        }
    }

    private void createTableIfNotExists(TableData tableData) {
        try {
            dynamoDb.describeTable(r -> r.tableName(modelsTableName));
        } catch (ResourceNotFoundException e) {
            CreateTableRequest createRequest = CreateTableRequest.builder()
                .tableName(modelsTableName)
                .keySchema(tableData.getKeySchema())
                .attributeDefinitions(tableData.getAttributeDefinitions())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            dynamoDb.createTable(createRequest);
        }
    }

    private void waitForTableActive() {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder()
                .tableName(modelsTableName)
                .build();

            int attempts = 0;
            while (attempts < 10) {
                TableDescription table = dynamoDb.describeTable(request).table();
                if (TableStatus.ACTIVE.toString().equals(table.tableStatusAsString())) {
                    return;
                }
                Thread.sleep(1000);
                attempts++;
            }
            throw new RuntimeException("Table did not become active in time");
        } catch (Exception e) {
            throw new RuntimeException("Error waiting for table to become active", e);
        }
    }

    private <T> T loadJsonResource(String path, Class<T> type) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + path);
            }
            return JsonbBuilder.create().fromJson(inputStream, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource: " + path, e);
        }
    }

    private Map<String, AttributeValue> convertToAttributeMap(ModelItem item) {
        return Map.ofEntries(
            Map.entry("model", AttributeValue.builder().s(item.getModel()).build()),
            Map.entry("name", AttributeValue.builder().s(item.getName()).build()),
            Map.entry("features", AttributeValue.builder().ss(item.getFeatures()).build()),
            Map.entry("image", AttributeValue.builder().s(item.getImage()).build()),
            Map.entry("description", AttributeValue.builder().s(item.getDescription()).build())
        );
    }

    public static class TableData {
        @JsonbProperty("TableName")
        private String tableName;

        @JsonbProperty("KeySchema")
        private List<KeySchemaWrapper> keySchema;

        @JsonbProperty("AttributeDefinitions")
        private List<AttributeDefinitionWrapper> attributeDefinitions;

        @JsonbProperty("ProvisionedThroughput")
        private ProvisionedThroughput provisionedThroughput;

        @JsonbProperty("Items")
        private List<ModelItem> items;

        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }

        public List<KeySchemaElement> getKeySchema() {
            return keySchema.stream()
                .map(k -> KeySchemaElement.builder()
                    .attributeName(k.getAttributeName())
                    .keyType(k.getKeyType())
                    .build())
                .toList();
        }
        public void setKeySchema(List<KeySchemaWrapper> keySchema) { this.keySchema = keySchema; }

        public List<AttributeDefinition> getAttributeDefinitions() {
            return attributeDefinitions.stream()
                .map(a -> AttributeDefinition.builder()
                    .attributeName(a.getAttributeName())
                    .attributeType(a.getAttributeType())
                    .build())
                .toList();
        }
        public void setAttributeDefinitions(List<AttributeDefinitionWrapper> attributeDefinitions) {
            this.attributeDefinitions = attributeDefinitions;
        }

        public List<ModelItem> getItems() { return items; }
        public void setItems(List<ModelItem> items) { this.items = items; }
    }

    public static class KeySchemaWrapper {
        @JsonbProperty("AttributeName")
        private String attributeName;

        @JsonbProperty("KeyType")
        private String keyType;

        public String getAttributeName() { return attributeName; }
        public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
        public KeyType getKeyType() { return KeyType.fromValue(keyType); }
        public void setKeyType(String keyType) { this.keyType = keyType; }
    }

    public static class AttributeDefinitionWrapper {
        @JsonbProperty("AttributeName")
        private String attributeName;

        @JsonbProperty("AttributeType")
        private String attributeType;

        public String getAttributeName() { return attributeName; }
        public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
        public ScalarAttributeType getAttributeType() { return ScalarAttributeType.fromValue(attributeType); }
        public void setAttributeType(String attributeType) { this.attributeType = attributeType; }
    }


    public static class ModelItem {
        @JsonbProperty("model")
        private String model;

        @JsonbProperty("name")
        private String name;

        @JsonbProperty("features")
        private List<String> features;

        @JsonbProperty("image")
        private String image;

        @JsonbProperty("description")
        private String description;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<String> getFeatures() { return features; }
        public void setFeatures(List<String> features) { this.features = features; }

        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}

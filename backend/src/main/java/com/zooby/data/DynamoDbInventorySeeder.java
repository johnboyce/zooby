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
public class DynamoDbInventorySeeder {

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "zooby.inventory.table")
    String inventoryTableName;

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    @PostConstruct
    void seedData() {
        try {
            TableData tableData = loadJsonResource("/schema/inventory.json", TableData.class);

            if (!activeProfile.equals("prod")) {
                createTableIfNotExists(tableData);
                waitForTableActive();
            }

            if (tableData.getItems() != null) {
                tableData.getItems().forEach(item -> {
                    try {
                        PutItemRequest request = PutItemRequest.builder()
                            .tableName(inventoryTableName)
                            .item(convertToAttributeMap(item))
                            .build();
                        dynamoDb.putItem(request);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to insert item: " + item.getSerialNumber(), e);
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed DynamoDB data", e);
        }
    }

    private void createTableIfNotExists(TableData tableData) {
        try {
            dynamoDb.describeTable(r -> r.tableName(inventoryTableName));
        } catch (ResourceNotFoundException e) {
            CreateTableRequest createRequest = CreateTableRequest.builder()
                .tableName(inventoryTableName)
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
                .tableName(inventoryTableName)
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

    private Map<String, AttributeValue> convertToAttributeMap(InventoryItem item) {
        return Map.of(
            "serial_number", AttributeValue.builder().s(item.getSerialNumber()).build(),
            "mac_address", AttributeValue.builder().s(item.getMacAddress()).build(),
            "model", AttributeValue.builder().s(item.getModel()).build(),
            "added_at", AttributeValue.builder().s(item.getAddedAt()).build()
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
        private List<InventoryItem> items;

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

        public ProvisionedThroughput getProvisionedThroughput() { return provisionedThroughput; }
        public void setProvisionedThroughput(ProvisionedThroughput provisionedThroughput) {
            this.provisionedThroughput = provisionedThroughput;
        }

        public List<InventoryItem> getItems() { return items; }
        public void setItems(List<InventoryItem> items) { this.items = items; }
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

    public static class InventoryItem {
        @JsonbProperty("serial_number")
        private String serialNumber;
        @JsonbProperty("mac_address")
        private String macAddress;
        @JsonbProperty("model")
        private String model;
        @JsonbProperty("added_at")
        private String addedAt;

        public String getSerialNumber() { return serialNumber; }
        public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getAddedAt() { return addedAt; }
        public void setAddedAt(String addedAt) { this.addedAt = addedAt; }
    }
}

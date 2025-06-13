package com.zooby.data;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Startup
@ApplicationScoped
public class DynamoDbSeeder {

    private static final Logger LOG = Logger.getLogger(DynamoDbSeeder.class);

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "zooby.models.table")
    String modelsTableName;

    @ConfigProperty(name = "zooby.inventory.table")
    String inventoryTableName;

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    @ConfigProperty(name = "zooby.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    @PostConstruct
    void seedData() {
        if (!seedEnabled) {
            LOG.info("⏭️  Skipping DynamoDB seed (zooby.seed.enabled=false)");
            return;
        }

        if ("prod".equalsIgnoreCase(activeProfile)) {
            LOG.info("⛔ Seeding disabled in production profile");
            return;
        }

        seedModels();
        seedInventory();
    }


    private void seedModels() {
        try {
            TableData<ModelItem> tableData = loadJsonResource("/schema/models.json", TableData.class);
            createTableIfNotExists(modelsTableName, tableData);
            waitForTableActive(modelsTableName);

            for (ModelItem item : tableData.getItems()) {
                try {
                    dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(modelsTableName)
                        .item(Map.of(
                            "model", AttributeValue.builder().s(item.getModel()).build(),
                            "name", AttributeValue.builder().s(item.getName()).build(),
                            "features", AttributeValue.builder().ss(item.getFeatures()).build(),
                            "image", AttributeValue.builder().s(item.getImage()).build(),
                            "description", AttributeValue.builder().s(item.getDescription()).build()
                        ))
                        .build());
                } catch (Exception e) {
                    LOG.error("❌ Failed to insert model: " + item.getModel(), e);
                }
            }
            LOG.info("✅ Finished seeding models");
        } catch (Exception e) {
            LOG.error("❌ Error during model table setup or seed", e);
        }
    }

    private void seedInventory() {
        try {
            TableData<InventoryItem> tableData = loadJsonResource("/schema/inventory.json", TableData.class);
            createTableIfNotExists(inventoryTableName, tableData);
            waitForTableActive(inventoryTableName);

            for (InventoryItem item : tableData.getItems()) {
                try {
                    dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(inventoryTableName)
                        .item(Map.of(
                            "serial_number", AttributeValue.builder().s(item.getSerialNumber()).build(),
                            "mac_address", AttributeValue.builder().s(item.getMacAddress()).build(),
                            "model", AttributeValue.builder().s(item.getModel()).build(),
                            "added_at", AttributeValue.builder().s(item.getAddedAt()).build()
                        ))
                        .build());
                } catch (Exception e) {
                    LOG.error("❌ Failed to insert inventory: " + item.getSerialNumber(), e);
                }
            }
            LOG.info("✅ Finished seeding inventory");
        } catch (Exception e) {
            LOG.error("❌ Error during inventory table setup or seed", e);
        }
    }

    private void createTableIfNotExists(String tableName, TableData<?> tableData) {
        try {
            dynamoDb.describeTable(r -> r.tableName(tableName));
        } catch (ResourceNotFoundException e) {
            dynamoDb.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(tableData.getKeySchema())
                .attributeDefinitions(tableData.getAttributeDefinitions())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
        }
    }

    private void waitForTableActive(String tableName) {
        try {
            int attempts = 0;
            while (attempts++ < 10) {
                var status = dynamoDb.describeTable(r -> r.tableName(tableName)).table().tableStatus();
                if (TableStatus.ACTIVE == status) return;
                Thread.sleep(1000);
            }
            throw new RuntimeException("Timeout waiting for table to be active: " + tableName);
        } catch (Exception e) {
            LOG.error("⚠️ Error waiting for table to activate: " + tableName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> TableData<T> loadJsonResource(String path, Class<?> rawType) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("❌ Resource not found: " + path);
            return (TableData<T>) JsonbBuilder.create().fromJson(is, rawType);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load or parse resource: " + path, e);
        }
    }

    // === Shared Classes ===

    public static class TableData<T> {
        @JsonbProperty("KeySchema")
        List<KeySchemaWrapper> keySchema;

        @JsonbProperty("AttributeDefinitions")
        List<AttributeDefinitionWrapper> attributeDefinitions;

        @JsonbProperty("Items")
        List<T> items;

        public List<KeySchemaElement> getKeySchema() {
            return keySchema.stream()
                .map(k -> KeySchemaElement.builder()
                    .attributeName(k.getAttributeName())
                    .keyType(k.getKeyType())
                    .build())
                .toList();
        }

        public List<AttributeDefinition> getAttributeDefinitions() {
            return attributeDefinitions.stream()
                .map(a -> AttributeDefinition.builder()
                    .attributeName(a.getAttributeName())
                    .attributeType(a.getAttributeType())
                    .build())
                .toList();
        }

        public List<T> getItems() {
            return items;
        }
    }

    public static class KeySchemaWrapper {
        @JsonbProperty("AttributeName")
        String attributeName;

        @JsonbProperty("KeyType")
        String keyType;

        public String getAttributeName() { return attributeName; }
        public KeyType getKeyType() { return KeyType.fromValue(keyType); }
    }

    public static class AttributeDefinitionWrapper {
        @JsonbProperty("AttributeName")
        String attributeName;

        @JsonbProperty("AttributeType")
        String attributeType;

        public String getAttributeName() { return attributeName; }
        public ScalarAttributeType getAttributeType() { return ScalarAttributeType.fromValue(attributeType); }
    }

    public static class ModelItem {
        @JsonbProperty("model") String model;
        @JsonbProperty("name") String name;
        @JsonbProperty("features") List<String> features;
        @JsonbProperty("image") String image;
        @JsonbProperty("description") String description;

        public String getModel() { return model; }
        public String getName() { return name; }
        public List<String> getFeatures() { return features; }
        public String getImage() { return image; }
        public String getDescription() { return description; }
    }

    public static class InventoryItem {
        @JsonbProperty("serial_number") String serialNumber;
        @JsonbProperty("mac_address") String macAddress;
        @JsonbProperty("model") String model;
        @JsonbProperty("added_at") String addedAt;

        public String getSerialNumber() { return serialNumber; }
        public String getMacAddress() { return macAddress; }
        public String getModel() { return model; }
        public String getAddedAt() { return addedAt; }
    }
}

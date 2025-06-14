package com.zooby.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

            TableData<ModelItem> tableData = loadJsonResource("schema/models.json", new TypeReference<>() {});
            createTableIfNotExists(modelsTableName, tableData);
            waitForTableActive(modelsTableName);

            for (ModelItem item : tableData.items) {
                try {
                    dynamoDb.putItem(PutItemRequest.builder()
                            .tableName(modelsTableName)
                            .item(Map.of(
                                    "model", AttributeValue.fromS(item.model),
                                    "name", AttributeValue.fromS(item.name),
                                    "features", AttributeValue.fromSs(item.features),
                                    "image", AttributeValue.fromS(item.image),
                                    "description", AttributeValue.fromS(item.description)
                            ))
                            .build());
                } catch (Exception e) {
                    LOG.error("❌ Failed to insert model: " + item.model, e);
                }
            }
            LOG.info("✅ Finished seeding models");
        } catch (Exception e) {
            LOG.error("❌ Error during model table setup or seed", e);
        }
    }

    private void seedInventory() {
        try {
            TableData<InventoryItem> tableData = loadJsonResource("schema/inventory.json", new TypeReference<>() {});
            createTableIfNotExists(inventoryTableName, tableData);
            waitForTableActive(inventoryTableName);

            for (InventoryItem item : tableData.items) {
                try {
                    dynamoDb.putItem(PutItemRequest.builder()
                            .tableName(inventoryTableName)
                            .item(Map.of(
                                    "serial_number", AttributeValue.fromS(item.serialNumber),
                                    "mac_address", AttributeValue.fromS(item.macAddress),
                                    "model", AttributeValue.fromS(item.model),
                                    "added_at", AttributeValue.fromS(item.addedAt)
                            ))
                            .build());
                } catch (Exception e) {
                    LOG.error("❌ Failed to insert inventory: " + item.serialNumber, e);
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
                    .keySchema(tableData.getKeySchemaElements())
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

    private <T> TableData<T> loadJsonResource(String path, TypeReference<TableData<T>> typeRef) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("❌ Resource not found: " + path);
            return MAPPER.readValue(is, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load or parse resource: " + path, e);
        }
    }

    // === Data Classes ===
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TableData<T> {
        @JsonProperty("KeySchema")
        public List<KeySchemaWrapper> keySchema;

        @JsonProperty("AttributeDefinitions")
        public List<AttributeDefinitionWrapper> attributeDefinitions;

        @JsonProperty("Items")
        public List<T> items;

        public List<KeySchemaElement> getKeySchemaElements() {
            return keySchema.stream()
                    .map(k -> KeySchemaElement.builder()
                            .attributeName(k.attributeName)
                            .keyType(KeyType.fromValue(k.keyType))
                            .build())
                    .toList();
        }

        public List<AttributeDefinition> getAttributeDefinitions() {
            return attributeDefinitions.stream()
                    .map(a -> AttributeDefinition.builder()
                            .attributeName(a.attributeName)
                            .attributeType(ScalarAttributeType.fromValue(a.attributeType))
                            .build())
                    .toList();
        }
    }

    public static class KeySchemaWrapper {
        @JsonProperty("AttributeName")
        public String attributeName;

        @JsonProperty("KeyType")
        public String keyType;
    }

    public static class AttributeDefinitionWrapper {
        @JsonProperty("AttributeName")
        public String attributeName;

        @JsonProperty("AttributeType")
        public String attributeType;
    }

    public static class ModelItem {
        @JsonProperty("model") public String model;
        @JsonProperty("name") public String name;
        @JsonProperty("features") public List<String> features;
        @JsonProperty("image") public String image;
        @JsonProperty("description") public String description;
    }

    public static class InventoryItem {
        @JsonProperty("serial_number") public String serialNumber;
        @JsonProperty("mac_address") public String macAddress;
        @JsonProperty("model") public String model;
        @JsonProperty("added_at") public String addedAt;
    }
}

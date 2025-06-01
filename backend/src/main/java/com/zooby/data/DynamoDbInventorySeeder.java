package com.zooby.data;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Startup
@ApplicationScoped
public class DynamoDbSeeder {

    @Inject
    DynamoDbClient dynamoDb;

    @PostConstruct
    private void seedData() {
        try {
            // Read JSON files from resources
            InventoryData inventory = loadJsonResource("/schema/inventory.json", InventoryData.class);

            // Put items into DynamoDB
            inventory.getItems().forEach(item -> {
                PutItemRequest request = PutItemRequest.builder()
                    .tableName("zooby-local-inventory")
                    .item(convertToAttributeMap(item))
                    .build();
                dynamoDb.putItem(request);
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed DynamoDB data", e);
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

    private static class InventoryData {
        private List<InventoryItem> items;
        public List<InventoryItem> getItems() { return items; }
        public void setItems(List<InventoryItem> items) { this.items = items; }
    }

    private static class InventoryItem {
        private String serialNumber;
        private String macAddress;
        private String model;
        private String addedAt;

        // Getters and setters
        public String getSerialNumber() { return serialNumber; }
        public String getMacAddress() { return macAddress; }
        public String getModel() { return model; }
        public String getAddedAt() { return addedAt; }
    }
}

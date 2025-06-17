package com.zooby.repository;

import com.zooby.model.ZoobyModel;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZoobyModelRepository {

    private static final Logger logger = LoggerFactory.getLogger(ZoobyModelRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public ZoobyModelRepository(
        DynamoDbClient dynamoDbClient,
        @ConfigProperty(name = "zooby.models.table") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        logger.info("ZoobyModelRepository initialized with table: {}", tableName);
    }

    public List<ZoobyModel> findAll(String filter, int offset, int limit) {
        logger.debug("findAll called with filter: {}, offset: {}, limit: {}", filter, offset, limit);
        ScanRequest.Builder scanBuilder = ScanRequest.builder()
            .tableName(tableName)
            .limit(limit);

        if (filter != null && !filter.trim().isEmpty()) {
            logger.debug("Applying filter: {}", filter);
            String filterExpression = "contains(#model, :filterValue) OR contains(#name, :filterValue)";
            Map<String, String> attrNames = new HashMap<>();
            attrNames.put("#model", "model");
            attrNames.put("#name", "name");

            Map<String, AttributeValue> attrValues = new HashMap<>();
            attrValues.put(":filterValue", AttributeValue.builder().s(filter.toLowerCase()).build());

            scanBuilder.filterExpression(filterExpression)
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValues);
        }

        List<ZoobyModel> results = new ArrayList<>();
        ScanResponse response;
        Map<String, AttributeValue> lastKey = null;
        int currentOffset = 0;

        do {
            scanBuilder.exclusiveStartKey(lastKey);
            response = dynamoDbClient.scan(scanBuilder.build());
            logger.debug("Scan response received with {} items", response.items().size());

            if (currentOffset < offset) {
                currentOffset += response.items().size();
            } else {
                response.items().stream()
                    .map(this::mapToZoobyModel)
                    .forEach(results::add);
            }

            lastKey = response.lastEvaluatedKey();
        } while (lastKey != null && results.size() < limit);

        logger.info("findAll returned {} results", results.size());
        return results.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    public Optional<ZoobyModel> findByModel(String modelKey) {
        logger.debug("findByModel called with modelKey: {}", modelKey);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("model", AttributeValue.builder().s(modelKey).build());

        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        if (response.hasItem()) {
            logger.info("Model found for key: {}", modelKey);
            return Optional.of(mapToZoobyModel(response.item()));
        } else {
            logger.warn("No model found for key: {}", modelKey);
            return Optional.empty();
        }
    }

    private ZoobyModel mapToZoobyModel(Map<String, AttributeValue> item) {
        logger.debug("Mapping DynamoDB item to ZoobyModel");
        ZoobyModel model = new ZoobyModel();
        model.setModel(item.get("model").s());
        model.setName(getStringOrNull(item, "name"));
        model.setDescription(getStringOrNull(item, "description"));
        model.setImage(getStringOrNull(item, "image"));

        if (item.containsKey("features")) {
            try {
                List<String> features = item.get("features").ss(); // ✅ Fixed: read SS properly
                model.setFeatures(features);
            } catch (Exception e) {
                logger.error("Error mapping features, setting to empty list", e);
                model.setFeatures(new ArrayList<>());
            }
        } else {
            model.setFeatures(new ArrayList<>());
        }

        return model;
    }

    public void save(ZoobyModel model) {
        logger.debug("Saving model: {}", model.getModel());
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("model", AttributeValue.builder().s(model.getModel()).build());
        item.put("name", AttributeValue.builder().s(model.getName()).build());
        item.put("description", AttributeValue.builder().s(model.getDescription()).build());

        if (model.getImage() != null) {
            item.put("image", AttributeValue.builder().s(model.getImage()).build());
        }

        if (model.getFeatures() != null && !model.getFeatures().isEmpty()) {
            item.put("features", AttributeValue.builder().ss(model.getFeatures()).build()); // ✅ Store as SS
        }

        PutItemRequest putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();

        dynamoDbClient.putItem(putRequest);
        logger.info("Model saved successfully: {}", model.getModel());
    }

    private String getStringOrNull(Map<String, AttributeValue> item, String key) {
        if (!item.containsKey(key)) {
            return null;
        }
        AttributeValue value = item.get(key);
        try {
            return value.s();
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving string value for key: {}", key, e);
            return null;
        }
    }
}

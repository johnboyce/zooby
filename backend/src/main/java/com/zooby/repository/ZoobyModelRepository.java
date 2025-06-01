package com.zooby.repository;

import com.zooby.model.ZoobyModel;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public ZoobyModelRepository(
            DynamoDbClient dynamoDbClient,
            @ConfigProperty(name = "zooby.models.table") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public List<ZoobyModel> findAll(String filter, int offset, int limit) {
        ScanRequest.Builder scanBuilder = ScanRequest.builder()
                .tableName(tableName)
                .limit(limit);

        // Add filter if provided
        if (filter != null && !filter.trim().isEmpty()) {
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

            if (currentOffset < offset) {
                currentOffset += response.items().size();
            } else {
                response.items().stream()
                        .map(this::mapToZoobyModel)
                        .forEach(results::add);
            }

            lastKey = response.lastEvaluatedKey();
        } while (lastKey != null && results.size() < limit);

        return results.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }



    public Optional<ZoobyModel> findByModel(String modelKey) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("model", AttributeValue.builder().s(modelKey).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.hasItem() ? Optional.of(mapToZoobyModel(response.item())) : Optional.empty();
    }

    private ZoobyModel mapToZoobyModel(Map<String, AttributeValue> item) {
        ZoobyModel model = new ZoobyModel();
        model.setModel(item.get("model").s());
        model.setName(getStringOrNull(item, "name"));
        model.setDescription(getStringOrNull(item, "description"));
        model.setImage(getStringOrNull(item, "image"));

        // Improved features mapping
        if (item.containsKey("features")) {
            try {
                List<String> features = item.get("features").l().stream()
                        .map(AttributeValue::s)
                        .collect(Collectors.toList());
                model.setFeatures(features);
            } catch (IllegalArgumentException e) {
                model.setFeatures(new ArrayList<>());
            }
        } else {
            model.setFeatures(new ArrayList<>());
        }

        return model;
    }

    public void save(ZoobyModel model) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("model", AttributeValue.builder().s(model.getModel()).build());
        item.put("name", AttributeValue.builder().s(model.getName()).build());
        item.put("description", AttributeValue.builder().s(model.getDescription()).build());

        if (model.getImage() != null) {
            item.put("image", AttributeValue.builder().s(model.getImage()).build());
        }

        // Improved features saving
        if (model.getFeatures() != null && !model.getFeatures().isEmpty()) {
            List<AttributeValue> featureValues = model.getFeatures().stream()
                    .map(feature -> AttributeValue.builder().s(feature).build())
                    .collect(Collectors.toList());
            item.put("features", AttributeValue.builder().l(featureValues).build());
        }

        PutItemRequest putRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(putRequest);
    }

    private String getStringOrNull(Map<String, AttributeValue> item, String key) {
        if (!item.containsKey(key)) {
            return null;
        }
        AttributeValue value = item.get(key);
        try {
            return value.s();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

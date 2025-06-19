package com.zooby.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@ApplicationScoped
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Inject
    DynamoDbClient dynamoDb;

    @ConfigProperty(name = "zooby.users.table")
    String tableName;

    @ConfigProperty(name = "zooby.user.default-role")
    String defaultRole;

    public Set<String> getRoles(String provider, String providerId) {
        Map<String, AttributeValue> key = Map.of(
            "provider", AttributeValue.fromS(provider),
            "provider_id", AttributeValue.fromS(providerId)
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .indexName("provider-provider_id-index")
            .keyConditionExpression("provider = :p and provider_id = :pid")
            .expressionAttributeValues(Map.of(
                ":p", AttributeValue.fromS(provider),
                ":pid", AttributeValue.fromS(providerId)
            ))
            .limit(1)
            .build();

        try {
            QueryResponse response = dynamoDb.query(queryRequest);
            if (response.hasItems() && !response.items().isEmpty()) {
                Map<String, AttributeValue> item = response.items().get(0);
                if (item.containsKey("roles") && item.get("roles").hasL()) {
                    Set<String> roles = new HashSet<>();
                    for (AttributeValue av : item.get("roles").l()) {
                        roles.add(av.s());
                    }
                    return roles;
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to query roles for provider={}, id={}: {}", provider, providerId, e.getMessage(), e);
        }

        return Set.of();
    }

    public void createUserIfMissing(String provider, String providerId, String subject) {
        Map<String, AttributeValue> key = Map.of(
            "provider", AttributeValue.fromS(provider),
            "provider_id", AttributeValue.fromS(providerId)
        );

        QueryRequest queryRequest = QueryRequest.builder()
            .tableName(tableName)
            .indexName("provider-provider_id-index")
            .keyConditionExpression("provider = :p and provider_id = :pid")
            .expressionAttributeValues(Map.of(
                ":p", AttributeValue.fromS(provider),
                ":pid", AttributeValue.fromS(providerId)
            ))
            .limit(1)
            .build();

        try {
            QueryResponse response = dynamoDb.query(queryRequest);
            if (response.hasItems() && !response.items().isEmpty()) {
                return; // user exists
            }
        } catch (Exception e) {
            LOG.error("Failed to check user existence: {}", e.getMessage(), e);
            return;
        }

        String userId = subject != null ? subject : UUID.randomUUID().toString();
        List<AttributeValue> defaultRoles = List.of(AttributeValue.fromS(defaultRole));

        Map<String, AttributeValue> item = Map.of(
            "user_id", AttributeValue.fromS(userId),
            "provider", AttributeValue.fromS(provider),
            "provider_id", AttributeValue.fromS(providerId),
            "roles", AttributeValue.fromL(defaultRoles)
        );

        PutItemRequest putRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .conditionExpression("attribute_not_exists(provider)")
            .build();

        try {
            dynamoDb.putItem(putRequest);
            LOG.info("Created new user {} (provider={}, id={})", userId, provider, providerId);
        } catch (ConditionalCheckFailedException e) {
            // race condition; another request created it
            LOG.warn("User already exists, skipping creation (provider={}, id={})", provider, providerId);
        } catch (Exception e) {
            LOG.error("Failed to create user: {}", e.getMessage(), e);
        }
    }
}

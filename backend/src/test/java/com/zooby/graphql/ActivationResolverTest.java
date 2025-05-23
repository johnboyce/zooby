package com.zooby.graphql;

import com.zooby.dynamodb.DynamoDBService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ActivationResolverTest {

    @InjectMock
    DynamoDBService dynamoService;

    @Inject
    ActivationResolver activationResolver;

    @Test
    void testActivationStatusFound() {
        String transactionId = "txn-123";

        Map<String, AttributeValue> fakeItem = Map.of(
                "macAddress", AttributeValue.fromS("AA:BB:CC:DD:EE"),
                "transactionId", AttributeValue.fromS(transactionId),
                "userId", AttributeValue.fromS("user-456"),
                "status", AttributeValue.fromS("ACTIVE"),
                "stepsLog", AttributeValue.fromSs(List.of("Initialized", "Connected")),
                "updatedAt", AttributeValue.fromS("2025-05-23T12:34:56Z")
        );

        when(dynamoService.getActivationStatus(transactionId)).thenReturn(fakeItem);

        ActivationStatus status = activationResolver.activationStatus(transactionId);

        assertNotNull(status);
        assertEquals("AA:BB:CC:DD:EE", status.macAddress());
        assertEquals("txn-123", status.transactionId());
        assertEquals("user-456", status.userId());
        assertEquals("ACTIVE", status.status());
        assertEquals(List.of("Initialized", "Connected"), status.stepsLog());
        assertEquals("2025-05-23T12:34:56Z", status.updatedAt());
    }

    @Test
    void testActivationStatusNotFound() {
        String transactionId = "non-existent";
        when(dynamoService.getActivationStatus(transactionId)).thenReturn(null);

        ActivationStatus status = activationResolver.activationStatus(transactionId);
        assertNull(status);
    }
}

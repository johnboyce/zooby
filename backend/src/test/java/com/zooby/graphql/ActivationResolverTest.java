package com.zooby.graphql;

import com.zooby.repository.DynamoDBService;
import com.zooby.model.ActivationStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestSecurity(user = "test-user", roles = { "customer" })
class ActivationResolverTest {

    @InjectMock
    DynamoDBService dynamoService;

    @Inject
    ActivationResolver activationResolver;

    @BeforeAll
    static void enableLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

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

package com.zooby.repository;

import com.zooby.model.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryRepositoryTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryRepository = new InventoryRepository(dynamoDbClient, "test-table");
    }

    @Test
    void findAll() {
        // Arrange
        ScanResponse mockResponse = ScanResponse.builder()
            .items(List.of(
                Map.of("serialNumber", AttributeValue.builder().s("SN-ZBX-400-0016").build(),
                    "model", AttributeValue.builder().s("ZBX-400").build(),
                    "macAddress", AttributeValue.builder().s("02:04:00:0f:5A:42").build())
            ))
            .build();
        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(mockResponse);

        // Act
        List<InventoryItem> items = inventoryRepository.findAll();

        // Assert
        assertEquals(1, items.size());
        assertEquals("SN-ZBX-400-0016", items.get(0).getSerialNumber());
    }

    @Test
    void find() {
        // Arrange
        GetItemResponse mockResponse = GetItemResponse.builder()
            .item(Map.of("serialNumber", AttributeValue.builder().s("SN-ZBX-400-0016").build(),
                "model", AttributeValue.builder().s("ZBX-400").build(),
                "macAddress", AttributeValue.builder().s("02:04:00:0f:5A:42").build()))
            .build();
        when(dynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(mockResponse);

        // Act
        Optional<InventoryItem> item = inventoryRepository.find("serialNumber", "SN-ZBX-400-0016");

        // Assert
        assertTrue(item.isPresent());
        assertEquals("SN-ZBX-400-0016", item.get().getSerialNumber());
    }

    @Test
    void testFind() {
        // Arrange
        ScanResponse mockResponse = ScanResponse.builder()
            .items(List.of(
                Map.of("serialNumber", AttributeValue.builder().s("SN-ZBX-400-0016").build(),
                    "model", AttributeValue.builder().s("ZBX-400").build(),
                    "macAddress", AttributeValue.builder().s("02:04:00:0f:5A:42").build())
            ))
            .build();
        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(mockResponse);

        // Act
        List<InventoryItem> items = inventoryRepository.find("model", "ZBX-400", 0, 10);

        // Assert
        assertEquals(1, items.size());
        assertEquals("ZBX-400", items.get(0).getModel());
    }

    @Test
    void findByModel() {
        // Arrange
        QueryResponse mockResponse = QueryResponse.builder()
            .items(List.of(
                Map.of("serialNumber", AttributeValue.builder().s("SN-ZBX-400-0016").build(),
                    "model", AttributeValue.builder().s("ZBX-400").build(),
                    "macAddress", AttributeValue.builder().s("02:04:00:0f:5A:42").build())
            ))
            .build();
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // Act
        List<InventoryItem> items = inventoryRepository.findByModel("ZBX-400", 0, 10);

        // Assert
        assertEquals(1, items.size());
        assertEquals("ZBX-400", items.get(0).getModel());
    }
}

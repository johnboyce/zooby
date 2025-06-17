package com.zooby.graphql;

import com.zooby.model.ZoobyModel;
import com.zooby.repository.ZoobyModelRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@QuarkusTest
class ModelResourceTest {
    private static final Logger LOG = LoggerFactory.getLogger(ModelResourceTest.class);
    private static final String TABLE_NAME = "zooby-local-models";
    private static final String TEST_MODEL = "ZB-Alpha";
    private static boolean tableInitialized = false;

    @Inject
    DynamoDbClient dynamoDbClient;

    @Inject
    ZoobyModelRepository modelRepository;

    @BeforeAll
    static void initializeOnce() {
        LOG.info("Test initialization starting...");
        tableInitialized = false;
    }

    @BeforeEach
    void setup() {
        if (!tableInitialized) {
            ensureTableExists();
            tableInitialized = true;
        }
        LOG.info("Clearing and seeding test data...");
        clearTable();
        createTestData();
    }

    private void ensureTableExists() {
        LOG.info("Ensuring DynamoDB table exists: {}", TABLE_NAME);
        try {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(List.of(KeySchemaElement.builder()
                    .attributeName("model")
                    .keyType(KeyType.HASH)
                    .build()))
                .attributeDefinitions(List.of(AttributeDefinition.builder()
                    .attributeName("model")
                    .attributeType(ScalarAttributeType.S)
                    .build()))
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

            LOG.info("Creating table with request: {}", createTableRequest);
            dynamoDbClient.createTable(createTableRequest);
            LOG.info("Table creation initiated, waiting for ACTIVE status...");
            waitForTableStatus(TableStatus.ACTIVE);
            LOG.info("Table successfully created and active");
        } catch (ResourceInUseException e) {
            LOG.info("Table already exists, ensuring it's active...");
            waitForTableStatus(TableStatus.ACTIVE);
        }
    }

    private void waitForTableStatus(TableStatus expectedStatus) {
        DescribeTableRequest request = DescribeTableRequest.builder()
            .tableName(TABLE_NAME)
            .build();

        LOG.info("Waiting for table status: {}", expectedStatus);
        Awaitility.await()
            .atMost(Duration.ofSeconds(30))
            .pollInterval(Duration.ofSeconds(2))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> {
                DescribeTableResponse response = dynamoDbClient.describeTable(request);
                TableStatus currentStatus = response.table().tableStatus();
                LOG.info("Current table status: {}", currentStatus);
                if (currentStatus != expectedStatus) {
                    throw new AssertionError(
                        String.format("Table status is {}, expecting {}", currentStatus, expectedStatus));
                }
            });
    }

    private void clearTable() {
        LOG.info("Clearing all data from table: {}", TABLE_NAME);
        ScanRequest scanRequest = ScanRequest.builder()
            .tableName(TABLE_NAME)
            .build();

        ScanResponse scan = dynamoDbClient.scan(scanRequest);
        scan.items().forEach(item -> {
            String modelId = item.get("model").s();
            LOG.info("Deleting item with model: {}", modelId);
            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("model", AttributeValue.builder().s(modelId).build()))
                .build();
            dynamoDbClient.deleteItem(deleteRequest);
        });
        LOG.info("Table cleared successfully");
    }

    private void createTestData() {
        LOG.info("Creating test model: {}", TEST_MODEL);
        ZoobyModel model = new ZoobyModel();
        model.setModel(TEST_MODEL);
        model.setName("Alpha model");
        model.setDescription("Top-tier futuristic device");
        model.setFeatures(List.of("usb-c", "led-ring"));
        model.setImage("https://example.com/alpha-front.jpg");

        modelRepository.save(model);
        LOG.info("Test data created successfully");
    }

    @Test
    @Disabled
    void testLookupModel() {
        LOG.info("Testing model lookup for: {}", TEST_MODEL);
        String query = """
            {
                zoobyModel(model: "{}") {
                    name
                    description
                    features
                }
            }""".formatted(TEST_MODEL);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
            .when().post("/graphql")
            .then()
            .statusCode(200)
            .body("data.zoobyModel.name", equalTo("Alpha model"))
            .body("data.zoobyModel.features.size()", equalTo(2));
        LOG.info("Model lookup test completed successfully");
    }

    @Test
    @Disabled
    void testPaginationAndFilter() {
        LOG.info("Testing pagination and filtering");
        String query = """
            {
                zoobyModels(filter: "alpha", offset: 0, limit: 2) {
                    model
                    name
                    features
                }
            }""";

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
            .when().post("/graphql")
            .then()
            .statusCode(200)
            .body("data.zoobyModels.size()", lessThanOrEqualTo(2));
        LOG.info("Pagination and filter test completed successfully");
    }
}

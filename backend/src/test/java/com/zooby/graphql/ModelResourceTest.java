package com.zooby.graphql;

import com.zooby.model.ZoobyModel;
import com.zooby.repository.ZoobyModelRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ModelResourceTest {

    @Inject
    DynamoDbEnhancedClient enhancedClient;

    DynamoDbTable<ZoobyModel> table;

    @BeforeEach
    void setup() {
        table = enhancedClient.table("zooby-local-models", TableSchema.fromBean(ZoobyModel.class));

        ZoobyModel model = new ZoobyModel();
        model.setModel("ZB-Alpha");
        model.setName("Alpha model");
        model.setDescription("Top-tier futuristic device");
        model.setWeight(".2 lb");
        model.setDimensions("5x5x2 cm");
        model.setYearMade(2024);
        model.setFeatures(List.of("usb-c", "led-ring"));
        model.setImageFrontUrl("https://example.com/alpha-front.jpg");
        model.setImageBackUrl("https://example.com/alpha-back.jpg");

        // table.putItem(model);
    }


//    @Test
//    void testLookupModel() {
//        given()
//            .contentType(ContentType.JSON)
//            .body("{ query: \"{ model(model: \\\"ZB-1001\\\") { name description } }\" }")
//            .when().post("/graphql")
//            .then().statusCode(200)
//            .body("data.model.name", equalTo("Zooby Pulse"));
//    }
//
//    @Test
//    void testPaginationAndFilter() {
//        given()
//            .contentType(ContentType.JSON)
//            .body("{ query: \"{ models(filter:\\\"pulse\\\", offset:0, limit:2) { model name } }\" }")
//            .when().post("/graphql")
//            .then().statusCode(200)
//            .body("data.models.size()", lessThanOrEqualTo(2));
//    }
}

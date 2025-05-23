package com.zooby.graphql;

import com.zooby.dynamodb.DynamoDBService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@GraphQLApi
public class ActivationResolver {

    @Inject
    DynamoDBService dynamoService;

    @Query
    public ActivationStatus activationStatus(@Name("transactionId") String transactionId) {
        Map<String, AttributeValue> item = dynamoService.getActivationStatus(transactionId);
        if (item == null) return null;

        return new ActivationStatus(
                item.get("macAddress").s(),
                item.get("transactionId").s(),
                item.get("userId").s(),
                item.get("status").s(),
                item.containsKey("stepsLog") ? item.get("stepsLog").ss() : List.of(),
                item.get("updatedAt").s()
        );
    }

    @Query
    public EligibilityResult eligibility(@Name("macAddress") String macAddress) {
        // Simulated logic; could be replaced with actual lookup
        return new EligibilityResult(macAddress, true, "ZoobyCorp", "ModelZ-9000");
    }

    @Mutation
    public ActivationResponse activate(@Name("macAddress") String macAddress,
                                       @Name("make") String make,
                                       @Name("model") String model) {
        String transactionId = "txn-" + macAddress.replace(":", "") + "-" + Instant.now().getEpochSecond();

        dynamoService.writeActivationStatus(Map.of(
                "macAddress", AttributeValue.fromS(macAddress),
                "transactionId", AttributeValue.fromS(transactionId),
                "userId", AttributeValue.fromS("user123"),
                "status", AttributeValue.fromS("INPROGRESS"),
                "updatedAt", AttributeValue.fromS(Instant.now().toString()),
                "make", AttributeValue.fromS(make),
                "model", AttributeValue.fromS(model),
                "stepsLog", AttributeValue.fromSs("Activation started")
        ));

        return new ActivationResponse(transactionId, true);
    }
}

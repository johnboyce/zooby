package com.zooby.graphql;

import com.zooby.dynamodb.DynamoDBService;
import com.zooby.model.ActivationResponse;
import com.zooby.model.ActivationStatus;
import com.zooby.model.EligibilityResult;
import jakarta.annotation.security.RolesAllowed;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@GraphQLApi
public class ActivationResolver {

    private static final Logger LOG = Logger.getLogger(ActivationResolver.class);

    @Inject
    DynamoDBService dynamoService;

    @Query
    @RolesAllowed("Customer")
    public ActivationStatus activationStatus(@Name("transactionId") String transactionId) {
        LOG.infof("Fetching activation status for transactionId=%s", transactionId);

        try {
            Map<String, AttributeValue> item = dynamoService.getActivationStatus(transactionId);

            if (item == null) {
                LOG.warnf("No activation status found for transactionId=%s", transactionId);
                return null;
            }

            ActivationStatus status = new ActivationStatus(
                    item.get("macAddress").s(),
                    item.get("transactionId").s(),
                    item.get("userId").s(),
                    item.get("status").s(),
                    item.containsKey("stepsLog") ? item.get("stepsLog").ss() : List.of(),
                    item.get("updatedAt").s()
            );

            LOG.debugf("Resolved activation status: %s", status);
            return status;

        } catch (Exception e) {
            LOG.errorf("Error fetching activation status for transactionId=%s: %s", transactionId, e.getMessage());
            throw new RuntimeException("Failed to fetch activation status", e);
        }
    }


    @Query
    @RolesAllowed("Manager")
    public EligibilityResult eligibility(@Name("macAddress") String macAddress) {
        // Simulated logic; could be replaced with actual lookup
        return new EligibilityResult(macAddress, true, "ZoobyCorp", "ModelZ-9000");
    }

    @Mutation
    @RolesAllowed("Manager")
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
                "stepsLog", AttributeValue.fromSs(List.of("Activation started"))
        ));

        return new ActivationResponse(transactionId, true);
    }
}

package com.zooby.graphql;

import com.zooby.model.ActivationResponse;
import com.zooby.model.ActivationStatus;
import com.zooby.model.Eligibility;
import com.zooby.repository.DynamoDBService;
import com.zooby.security.UserContext;
import io.quarkus.security.ForbiddenException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@GraphQLApi
public class ActivationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ActivationResolver.class);

    @Inject
    DynamoDBService dynamoService;

    @Inject
    UserContext user;

    @Query
    @RolesAllowed("customer")
    public ActivationStatus activationStatus(@Name("transactionId") String transactionId) {
        LOG.info("ActivationStatus: userId={}, account={}, roles={}",
            user.getUserId(), user.getAccount(), user.getRoles());

        try {
            LOG.debug("Fetching activation status for transactionId={}", transactionId);
            Map<String, AttributeValue> item = dynamoService.getActivationStatus(transactionId);

            if (item == null) {
                LOG.warn("No activation status found for transactionId={}", transactionId);
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

            LOG.debug("Resolved activation status: {}", status);
            return status;

        } catch (Exception e) {
            LOG.error("Error fetching activation status for transactionId={}: {}", transactionId, e.getMessage());
            throw new RuntimeException("Failed to fetch activation status", e);
        }
    }

    @Query
    @RolesAllowed({"manager", "admin"})
    public Eligibility eligibility(@Name("macAddress") String macAddress) {
        LOG.info("Eligibility check for macAddress={} by userId={}", macAddress, user.getUserId());
        if (!user.hasCapability("restart")) {
            LOG.warn("Unauthorized activation attempt by user {}", user.getUserId());
            throw new ForbiddenException("You do not have permission to activate a Zooby.");
        }
        LOG.debug("Eligibility check passed for macAddress={}", macAddress);
        return new Eligibility(macAddress, true, "ZoobyCorp", "ModelZ-9000");
    }

    @Mutation
    @RolesAllowed("manager, admin")
    public ActivationResponse activate(@Name("macAddress") String macAddress,
                                       @Name("make") String make,
                                       @Name("model") String model) {
        String transactionId = "txn-" + macAddress.replace(":", "") + "-" + Instant.now().getEpochSecond();

        LOG.info("Activating device with macAddress={}, make={}, model={} by userId={}",
            macAddress, make, model, user.getUserId());
        try {
            LOG.debug("Writing activation status for transactionId={}", transactionId);
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
            LOG.info("Activation successful for transactionId={}", transactionId);
            return new ActivationResponse(transactionId, true);
        } catch (Exception e) {
            LOG.error("Error activating device with macAddress={}: {}", macAddress, e.getMessage());
            throw new RuntimeException("Failed to activate device", e);
        }
    }
}

package com.zooby.graphql;

import com.zooby.repository.DynamoDBService;
import com.zooby.model.ActivationResponse;
import com.zooby.model.ActivationStatus;
import com.zooby.model.Eligibility;
import com.zooby.security.UserContext;
import io.quarkus.security.ForbiddenException;
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

    @Inject
    UserContext user;

    @Query
    @RolesAllowed("customer")
    public ActivationStatus activationStatus(@Name("transactionId") String transactionId) {
        LOG.infof("ActivationStatus: userId=%s, account=%s, roles=%s",
            user.getUserId(), user.getAccount(), user.getRoles());

        try {
            LOG.debugf("Fetching activation status for transactionId=%s", transactionId);
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
    @RolesAllowed({"manager", "admin"})
    public Eligibility eligibility(@Name("macAddress") String macAddress) {
        LOG.infof("Eligibility check for macAddress=%s by userId=%s", macAddress, user.getUserId());
        if (!user.hasCapability("restart")) {
            LOG.warnf("Unauthorized activation attempt by user %s", user.getUserId());
            throw new ForbiddenException("You do not have permission to activate a Zooby.");
        }
        LOG.debugf("Eligibility check passed for macAddress=%s", macAddress);
        return new Eligibility(macAddress, true, "ZoobyCorp", "ModelZ-9000");
    }

    @Mutation
    @RolesAllowed("manager, admin")
    public ActivationResponse activate(@Name("macAddress") String macAddress,
                                       @Name("make") String make,
                                       @Name("model") String model) {
        String transactionId = "txn-" + macAddress.replace(":", "") + "-" + Instant.now().getEpochSecond();

        LOG.infof("Activating device with macAddress=%s, make=%s, model=%s by userId=%s",
            macAddress, make, model, user.getUserId());
        try {
            LOG.debugf("Writing activation status for transactionId=%s", transactionId);
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
            LOG.infof("Activation successful for transactionId=%s", transactionId);
            return new ActivationResponse(transactionId, true);
        } catch (Exception e) {
            LOG.errorf("Error activating device with macAddress=%s: %s", macAddress, e.getMessage());
            throw new RuntimeException("Failed to activate device", e);
        }
    }
}

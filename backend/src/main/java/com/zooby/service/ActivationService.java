package com.zooby.service;

import com.zooby.model.ActivationResponse;
import com.zooby.model.ActivationStatus;
import com.zooby.model.EligibilityResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Arrays;

@ApplicationScoped
public class ActivationService {

    public ActivationStatus getActivationStatus(String transactionId) {
        return new ActivationStatus("AA:BB:CC:DD:EE:FF", transactionId, "user123", "INPROGRESS",
                Arrays.asList("Initiating contact with Zooby Central", "Configuring Zooby..."),
                "2025-05-23T22:00:00Z");
    }

    public EligibilityResult checkEligibility(String macAddress) {
        return new EligibilityResult(macAddress, true, "ZoobyCorp", "ModelZ-9000");
    }

    public ActivationResponse activate(String macAddress, String make, String model) {
        return new ActivationResponse("txn-" + macAddress.replace(":", ""), true);
    }
}

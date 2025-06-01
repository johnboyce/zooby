package com.zooby.service;

import com.zooby.model.ActivationResponse;
import com.zooby.model.ActivationStatus;
import com.zooby.model.Eligibility;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@ApplicationScoped
public class ActivationService {

    public ActivationStatus getActivationStatus(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }
        return new ActivationStatus(null, transactionId, null, "PENDING",
            new ArrayList<>(), LocalDateTime.now().toString());
    }

    public Eligibility checkEligibility(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new IllegalArgumentException("MAC address cannot be empty");
        }
        // Simple eligibility check based on MAC address format
        boolean isEligible = macAddress.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        return new Eligibility(macAddress, isEligible, null, null);
    }

    public ActivationResponse activate(String macAddress, String make, String model) {
        if (macAddress == null || model == null) {
            throw new IllegalArgumentException("MAC address and model are required");
        }
        String transactionId = UUID.randomUUID().toString();
        return new ActivationResponse(transactionId, true);
    }
}

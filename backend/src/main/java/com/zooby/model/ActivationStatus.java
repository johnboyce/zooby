package com.zooby.graphql;

import java.util.List;

public record ActivationStatus(
    String macAddress,
    String transactionId,
    String userId,
    String status,
    List<String> stepsLog,
    String updatedAt
) {}

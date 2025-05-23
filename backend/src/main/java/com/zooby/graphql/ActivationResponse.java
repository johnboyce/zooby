package com.zooby.graphql;

public record ActivationResponse(
    String transactionId,
    boolean accepted
) {}

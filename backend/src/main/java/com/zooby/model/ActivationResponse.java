package com.zooby.model;

public record ActivationResponse(
    String transactionId,
    boolean accepted
) {}

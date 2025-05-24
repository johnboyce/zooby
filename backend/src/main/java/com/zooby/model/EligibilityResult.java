package com.zooby.graphql;

public record EligibilityResult(
    String macAddress,
    boolean eligible,
    String make,
    String model
) {}

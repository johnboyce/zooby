package com.zooby.model;

public record EligibilityResult(
    String macAddress,
    boolean eligible,
    String make,
    String model
) {}

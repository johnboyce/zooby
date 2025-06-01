package com.zooby.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Type;

@Type
@Description("Represents device eligibility information")
@RegisterForReflection
public record Eligibility(
    @Description("MAC address of the device")
    @JsonbProperty("mac_address")
    String macAddress,

    @Description("Whether the device is eligible for the service")
    boolean eligible,

    @Description("Device manufacturer")
    String make,

    @Description("Model identifier of the device")
    String model
) {
}

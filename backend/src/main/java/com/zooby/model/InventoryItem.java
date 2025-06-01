package com.zooby.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Type;

@Type
@RegisterForReflection
public class InventoryItem {
    @Description("Unique serial number of the device")
    @JsonbProperty("serial_number")
    private String serialNumber;

    @Description("MAC address of the device")
    @JsonbProperty("mac_address")
    private String macAddress;

    @Description("Model identifier of the device")
    private String model;

    @Description("Timestamp when the device was added to inventory")
    @JsonbProperty("added_at")
    private String addedAt;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }
}

package com.zooby.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Type;

import java.util.List;

@Type
@RegisterForReflection
public class ZoobyModel {

    @Description("The unique identifier for this model")
    private String model;

    @Description("The full product name")
    private String name;

    @Description("Detailed marketing-style description")
    private String description;

    @Description("Technical feature list")
    private List<String> features;

    @Description("Link to the image of the device")
    @JsonbProperty("image")
    private String image;

    public ZoobyModel() {
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

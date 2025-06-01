package com.zooby.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Type;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Type
@RegisterForReflection
@DynamoDbBean
public class ZoobyModel {

    @Description("The unique identifier for this model")
    private String model;

    @Description("The full product name")
    private String name;

    @Description("Detailed marketing-style description")
    private String description;

    @Description("Technical feature list")
    private List<String> features;

    @Description("Year the model was first manufactured")
    private int yearMade;

    @Description("Device dimensions in mm (L x W x H)")
    private String dimensions;

    @Description("Device weight in grams")
    private String weight;

    @Description("Link to the front image of the device")
    @JsonbProperty("image_front_url")
    private String imageFrontUrl;

    @Description("Link to the back image of the device")
    @JsonbProperty("image_back_url")
    private String imageBackUrl;

    @Description("Link to a side-view image of the device")
    @JsonbProperty("image_side_url")
    private String imageSideUrl;

    @Description("Link to a zoomed-in product detail shot")
    @JsonbProperty("image_zoom_url")
    private String imageZoomUrl;

    public ZoobyModel() {
    }

    // Getters and Setters
    @DynamoDbPartitionKey
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

    public int getYearMade() {
        return yearMade;
    }

    public void setYearMade(int yearMade) {
        this.yearMade = yearMade;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageFrontUrl() {
        return imageFrontUrl;
    }

    public void setImageFrontUrl(String imageFrontUrl) {
        this.imageFrontUrl = imageFrontUrl;
    }

    public String getImageBackUrl() {
        return imageBackUrl;
    }

    public void setImageBackUrl(String imageBackUrl) {
        this.imageBackUrl = imageBackUrl;
    }

    public String getImageSideUrl() {
        return imageSideUrl;
    }

    public void setImageSideUrl(String imageSideUrl) {
        this.imageSideUrl = imageSideUrl;
    }

    public String getImageZoomUrl() {
        return imageZoomUrl;
    }

    public void setImageZoomUrl(String imageZoomUrl) {
        this.imageZoomUrl = imageZoomUrl;
    }

    public static ZoobyModel from(Map<String, AttributeValue> item) {
        ZoobyModel model = new ZoobyModel();
        model.setModel(item.get("model").s());
        model.setName(item.get("name").s());
        model.setDescription(item.get("description").s());

        // Split features string into list if stored as a comma-separated string
        model.setFeatures(Arrays.asList(
            Optional.ofNullable(item.get("features"))
                .map(AttributeValue::s)
                .orElse("")
                .split("\\s*,\\s*")
        ));

        model.setYearMade(Optional.ofNullable(item.get("yearMade")).map(a -> Integer.parseInt(a.n())).orElse(0));
        model.setDimensions(Optional.ofNullable(item.get("dimensions")).map(AttributeValue::s).orElse(""));
        model.setWeight(Optional.ofNullable(item.get("weight")).map(AttributeValue::s).orElse(""));
        model.setImageFrontUrl(Optional.ofNullable(item.get("imageFrontUrl")).map(AttributeValue::s).orElse(""));
        model.setImageBackUrl(Optional.ofNullable(item.get("imageBackUrl")).map(AttributeValue::s).orElse(""));
        model.setImageSideUrl(Optional.ofNullable(item.get("imageSideUrl")).map(AttributeValue::s).orElse(""));
        model.setImageZoomUrl(Optional.ofNullable(item.get("imageZoomUrl")).map(AttributeValue::s).orElse(""));

        return model;
    }
}

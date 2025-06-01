package com.zooby.rest;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/images")
public class ImageProxyResource {

    private static final String IMAGE_BASE_PATH = "public/images";

    @GET
    @Path("/{filename}")
    @Produces({"image/png", "image/jpeg", "image/webp", "image/gif"})
    public Response getImage(@PathParam("filename") String filename) {
        // Prevent path traversal attacks
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return Response.status(Status.BAD_REQUEST)
                .entity("Invalid filename").build();
        }

        try {
            java.nio.file.Path imagePath = Paths.get(IMAGE_BASE_PATH, filename);
            File imageFile = imagePath.toFile();

            if (!imageFile.exists() || !imageFile.isFile()) {
                return Response.status(Status.NOT_FOUND).build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);
            String mimeType = URLConnection.guessContentTypeFromName(filename);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            return Response.ok(imageBytes, mimeType)
                .header("Cache-Control", "max-age=86400")
                .build();

        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity("Failed to read image: " + e.getMessage())
                .build();
        }
    }
}

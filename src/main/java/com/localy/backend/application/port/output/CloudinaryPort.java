package com.localy.backend.application.port.output;

import java.io.IOException;

public interface CloudinaryPort {
    String upload(byte[] file, String publicId) throws IOException;

    default String upload(byte[] file, String publicId, String resourceType)
            throws IOException {
        return upload(file, publicId);
    }
}
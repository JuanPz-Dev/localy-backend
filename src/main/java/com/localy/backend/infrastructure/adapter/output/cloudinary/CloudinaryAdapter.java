package com.localy.backend.infrastructure.adapter.output.cloudinary;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.localy.backend.application.port.output.CloudinaryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CloudinaryAdapter implements CloudinaryPort{

    private final Cloudinary cloudinary;

    @Override
    public String upload(byte[] file, String fileName) throws IOException {
        return upload(file, fileName, "auto");
    }

    @Override
    public String upload(byte[] file, String fileName, String resourceType) throws IOException {
        Map  <? , ?> result = cloudinary.uploader().upload(
            file, ObjectUtils.asMap("public_id", fileName, "resource_type", resourceType)
        );
        return result.get("secure_url").toString();
    }
}
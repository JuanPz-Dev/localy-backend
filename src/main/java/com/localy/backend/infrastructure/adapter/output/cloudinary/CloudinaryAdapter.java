package com.localy.backend.infrastructure.adapter.output.cloudinary;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.localy.backend.application.port.output.CloudinaryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CloudinaryAdapter implements CloudinaryPort{

    private static final Pattern VERSION_SEGMENT = Pattern.compile("v\\d+");

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

    @Override
    public void deleteByUrl(String secureUrl) throws IOException {
        String publicId = publicIdFromUrl(secureUrl);
        Map<?, ?> result = cloudinary.uploader().destroy(
            publicId, ObjectUtils.asMap("resource_type", "image", "invalidate", true)
        );
        Object deletionResult = result.get("result");
        if (!"ok".equals(deletionResult) && !"not found".equals(deletionResult)) {
            throw new IOException("Cloudinary no pudo eliminar la imagen: " + deletionResult);
        }
    }

    private String publicIdFromUrl(String secureUrl) throws IOException {
        URI uri;
        try {
            uri = URI.create(secureUrl);
        } catch (IllegalArgumentException exception) {
            throw new IOException("La URL de Cloudinary no es válida", exception);
        }

        String path = uri.getPath();
        String host = uri.getHost();
        if (!"https".equalsIgnoreCase(uri.getScheme()) || path == null || host == null
                || (!host.equals("res.cloudinary.com") && !host.endsWith(".cloudinary.com"))) {
            throw new IOException("La URL no pertenece a Cloudinary");
        }

        int uploadIndex = path.indexOf("/image/upload/");
        if (uploadIndex < 0) {
            throw new IOException("La URL no corresponde a una imagen de Cloudinary");
        }

        String[] segments = path.substring(uploadIndex + "/image/upload/".length()).split("/");
        int versionIndex = -1;
        for (int index = 0; index < segments.length; index++) {
            if (VERSION_SEGMENT.matcher(segments[index]).matches()) {
                versionIndex = index;
                break;
            }
        }
        if (versionIndex < 0 || versionIndex == segments.length - 1) {
            throw new IOException("No se pudo identificar la referencia de la imagen en Cloudinary");
        }

        StringBuilder publicId = new StringBuilder();
        for (int index = versionIndex + 1; index < segments.length; index++) {
            if (publicId.length() > 0) {
                publicId.append('/');
            }
            publicId.append(segments[index]);
        }
        int extensionIndex = publicId.lastIndexOf(".");
        if (extensionIndex > publicId.lastIndexOf("/")) {
            publicId.delete(extensionIndex, publicId.length());
        }
        if (publicId.isEmpty()) {
            throw new IOException("La referencia de imagen de Cloudinary está vacía");
        }
        return publicId.toString();
    }
}
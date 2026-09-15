package com.localy.backend.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.localy.backend.application.dto.request.FileUpload;

@Component
public class MultipartFileToFileUploadMapper {

    public FileUpload toFileUpload(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        try {
            return new FileUpload(
                multipartFile.getBytes(),
                multipartFile.getOriginalFilename()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Error al procesar el archivo recibido", e);
        }
    }
}

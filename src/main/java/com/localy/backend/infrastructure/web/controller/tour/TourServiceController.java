package com.localy.backend.infrastructure.web.controller.tour;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.TourServiceFilterRequest;
import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;
import com.localy.backend.application.port.input.tour.CreateTourServiceUseCase;
import com.localy.backend.application.port.input.tour.DeleteTourServiceUseCase;
import com.localy.backend.application.port.input.tour.GetMyTourServicesUseCase;
import com.localy.backend.application.port.input.tour.GetTourServiceUseCase;
import com.localy.backend.application.port.input.tour.SearchTourServicesUseCase;
import com.localy.backend.application.port.input.tour.UpdateTourServiceUseCase;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Category;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourServiceController {
    
    private final GetTourServiceUseCase getTourServiceUseCase;
    private final CreateTourServiceUseCase createTourServiceUseCase;
    private final DeleteTourServiceUseCase deleteTourServiceUseCase;
    private final UpdateTourServiceUseCase updateTourServiceUseCase;
    private final SearchTourServicesUseCase searchTourServicesUseCase;
    private final ObjectMapper objectMapper;
    private final GetMyTourServicesUseCase getMyTourServicesUseCase;

    @PostMapping("/create")
    public ResponseEntity<TourServiceResponse> create(
        @RequestPart ("data") String data,
        @RequestPart ("photos") List<MultipartFile>photos) throws Exception{

            TourServiceRequest tourServiceRequest = objectMapper.readValue(data, TourServiceRequest.class);
            List<FileUpload> fileUploads = toFileUploads(photos);
        TourServiceResponse tourServiceResponse = createTourServiceUseCase.execute(tourServiceRequest,fileUploads);
        return ResponseEntity.status(HttpStatus.CREATED).body(tourServiceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourServiceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getTourServiceUseCase.execute(id));
    }

    @GetMapping
    public PageResult<TourServiceResponse> search(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) Category category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size)
    {
        TourServiceFilterRequest search = new TourServiceFilterRequest(category,city);
        return searchTourServicesUseCase.execute(search, new PaginationRequest(page,size));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TourServiceResponse>update(
        @PathVariable Long id,
        @RequestPart("data") String data,
        @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
        @RequestParam(value = "existingPhotos", required = false) List<String> existingPhotos
    ) throws Exception {
        TourServiceRequest tourServiceRequest = objectMapper.readValue(data, TourServiceRequest.class);
        List<FileUpload> fileUploads = toFileUploads(photos);
        TourServiceResponse tourServiceResponse = updateTourServiceUseCase.execute(
                id,
                tourServiceRequest,
                fileUploads,
                existingPhotos == null ? List.of() : existingPhotos
        );
        return ResponseEntity.status(HttpStatus.OK).body(tourServiceResponse);
    }

    private List<FileUpload> toFileUploads(List<MultipartFile> photos) {
        if (photos == null || photos.isEmpty()) {
            return List.of();
        }
        List<FileUpload> fileUploads = new ArrayList<>(photos.size());
        for (MultipartFile photo : photos) {
            try {
                fileUploads.add(new FileUpload(photo.getBytes(), photo.getOriginalFilename()));
            } catch (IOException exception) {
                throw new IllegalArgumentException("Error al procesar una fotografía", exception);
            }
        }
        return fileUploads;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void>delete(@PathVariable Long id){
        deleteTourServiceUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-tours")
    public ResponseEntity<List<TourServiceResponse>> getMyTourServices(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        List<TourServiceResponse> tourServices = getMyTourServicesUseCase.getMyTourServices(new PaginationRequest(page, size));
        return ResponseEntity.ok(tourServices);
    }

}
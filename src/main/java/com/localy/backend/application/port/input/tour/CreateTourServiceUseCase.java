package com.localy.backend.application.port.input.tour;

import java.util.List;

import com.localy.backend.application.dto.request.FileUpload;
import com.localy.backend.application.dto.request.TourServiceRequest;
import com.localy.backend.application.dto.response.TourServiceResponse;

public interface CreateTourServiceUseCase {
    TourServiceResponse execute(TourServiceRequest tourServiceRequest,List<FileUpload>photos);
}
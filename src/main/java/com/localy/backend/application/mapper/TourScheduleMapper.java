package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.application.dto.request.TourScheduleRequest;
import com.localy.backend.application.dto.response.TourScheduleResponse;
import com.localy.backend.domain.model.TourSchedule;

@Mapper(componentModel = "spring")
public interface TourScheduleMapper {
    TourSchedule toDomain(TourScheduleRequest tourScheduleRequest);
    TourScheduleResponse toResponse(TourSchedule tourSchedule);
}
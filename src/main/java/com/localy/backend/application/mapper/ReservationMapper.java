package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.application.dto.request.ReservationRequest;
import com.localy.backend.application.dto.response.ReservationResponse;
import com.localy.backend.domain.model.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "tourService", ignore = true)
    @Mapping(target = "reservationStatus", ignore = true)
    @Mapping(target = "basePrice", ignore = true)
    @Mapping(target = "commissionAmount", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "tourSchedule", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reservation toDomain(ReservationRequest reservationRequest);

    @Mapping(target = "tourId", source = "tourService.id")
    @Mapping(target = "tourName", source = "tourService.title")
    @Mapping(target = "guideName", source = "tourService.guideProfile.user.name")
    @Mapping(target = "touristName", source = "user.name")
    @Mapping(target = "adults", source = "adults")
    @Mapping(target = "children", source = "children")
    @Mapping(target = "status", source = "reservationStatus")
    @Mapping(target = "startTime", source = "tourSchedule.startTime")
    ReservationResponse toResponse(Reservation reservation);
}
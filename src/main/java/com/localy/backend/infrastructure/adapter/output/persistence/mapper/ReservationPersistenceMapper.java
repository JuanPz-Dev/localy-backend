package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.Reservation;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReservationEntity;

@Mapper(componentModel = "spring" , uses = {UserPersistenceMapper.class,
    TourServicePersistenceMapper.class,
    TourSchedulePersistenceMapper.class})
public interface ReservationPersistenceMapper {
    @Mapping(source = "tourService", target = "tourServiceEntity")
    @Mapping(source = "tourSchedule" , target = "tourScheduleEntity")
    ReservationEntity toEntity(Reservation reservation);
    @Mapping(source = "tourServiceEntity", target = "tourService")
    @Mapping(source = "tourScheduleEntity", target = "tourSchedule")
    Reservation toDomain(ReservationEntity reservationEntity);
}
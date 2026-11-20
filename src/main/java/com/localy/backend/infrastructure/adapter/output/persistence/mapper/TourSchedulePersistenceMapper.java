package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.TourSchedule;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.TourScheduleEntity;

@Mapper(componentModel = "spring")
public interface TourSchedulePersistenceMapper {
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "tourServiceEntity", ignore = true)
    TourScheduleEntity toEntity(TourSchedule tourSchedule);
    TourSchedule toDomain(TourScheduleEntity tourScheduleEntity);
}
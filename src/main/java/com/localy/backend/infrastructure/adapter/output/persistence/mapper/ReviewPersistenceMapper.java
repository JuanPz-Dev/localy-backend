package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;

import com.localy.backend.domain.model.Review;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.ReviewEntity;

@Mapper(componentModel = "spring", uses = UserPersistenceMapper.class)
public interface ReviewPersistenceMapper {
    ReviewEntity toEntity(Review review);
    Review toDomain (ReviewEntity reviewEntity);
}
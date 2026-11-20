package com.localy.backend.infrastructure.adapter.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.domain.model.Payment;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.PaymentEntity;

@Mapper(componentModel = "spring", uses = ReservationPersistenceMapper.class)
public interface PaymentPersistenceMapper {
    @Mapping(target = "reservation", ignore = true)
    PaymentEntity toEntity(Payment payment);
    Payment toDomain(PaymentEntity paymentEntity);
}

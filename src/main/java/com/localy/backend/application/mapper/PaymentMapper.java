package com.localy.backend.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.localy.backend.application.dto.request.PaymentRequest;
import com.localy.backend.application.dto.response.PaymentResponse;
import com.localy.backend.domain.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reservation", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "scheduledReleaseAt",ignore = true)
    @Mapping(target = "releasedAt", ignore = true)
    Payment toDomain(PaymentRequest paymentRequest);
    PaymentResponse toResponse(Payment payment);
}
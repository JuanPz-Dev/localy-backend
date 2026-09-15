package com.localy.backend.application.dto.request;

import com.localy.backend.domain.enums.PaymentMethod;

public record PaymentRequest(
    PaymentMethod paymentMethod
){}
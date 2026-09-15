package com.localy.backend.application.port.input.payment;

import com.localy.backend.application.dto.request.PaymentRequest;
import com.localy.backend.application.dto.response.PaymentResponse;

public interface ProcessPaymentUseCase {
    PaymentResponse execute (PaymentRequest paymentRequest);
}
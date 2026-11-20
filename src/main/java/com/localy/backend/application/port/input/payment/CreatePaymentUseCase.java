package com.localy.backend.application.port.input.payment;

import com.localy.backend.application.dto.request.PaymentRequest;
import com.localy.backend.application.dto.response.PaymentResponse;

public interface CreatePaymentUseCase {
    PaymentResponse execute(Long reservationId, PaymentRequest paymentRequest);
}

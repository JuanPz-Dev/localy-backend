package com.localy.backend.infrastructure.web.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.request.PaymentRequest;
import com.localy.backend.application.dto.response.PaymentResponse;
import com.localy.backend.application.port.input.payment.CreatePaymentUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    @PostMapping("/reservation/{reservationId}")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long reservationId,
            @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = createPaymentUseCase.execute(reservationId, paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }
}
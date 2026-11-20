package com.localy.backend.application.usecase.payment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.localy.backend.application.dto.request.PaymentRequest;
import com.localy.backend.application.dto.response.PaymentResponse;
import com.localy.backend.application.exception.BusinessRuleException;
import com.localy.backend.application.mapper.PaymentMapper;
import com.localy.backend.application.port.input.payment.CreatePaymentUseCase;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.security.CurrentUserPort;
import com.localy.backend.domain.enums.PaymentStatus;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Payment;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.User;
import com.localy.backend.domain.repository.PaymentRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

    private final CurrentUserPort currentUserPort;
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse execute(Long reservationId, PaymentRequest paymentRequest) {
        if (paymentRequest == null || paymentRequest.paymentMethod() == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }

        User currentUser = currentUserPort.getCurrentUser();
        Reservation reservation = reservationRepositoryPort.findById(reservationId)
            .orElseThrow(() -> new BusinessRuleException("La reserva no existe"));

        if (reservation.getUser() == null
                || !Objects.equals(reservation.getUser().getId(), currentUser.getId())) {
            throw new BusinessRuleException("No tienes permiso para pagar esta reserva");
        }

        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new BusinessRuleException("Solo se pueden pagar reservas pendientes");
        }

        Instant now = Instant.now();
        Payment payment = paymentMapper.toDomain(paymentRequest);
        payment.setReservation(reservation);
        payment.setAmount(reservation.getTotalPrice());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setTransactionId("SIM-" + UUID.randomUUID());
        payment.setCreatedAt(now);
        payment.setPaidAt(now);

        reservation.setReservationStatus(ReservationStatus.PAID);

        Payment savedPayment = paymentRepositoryPort.save(payment);
        reservationRepositoryPort.save(reservation);

        return paymentMapper.toResponse(savedPayment);
    }
}

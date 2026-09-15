package com.localy.backend.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceResponse(
    Long id,
    Long reservationId,
    String invoiceNumber,
    BigDecimal subtotal,
    BigDecimal total,
    Instant issuedAt
){}
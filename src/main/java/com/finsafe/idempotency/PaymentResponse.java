package com.finsafe.idempotency;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
    String status,
    String message,
    String transactionId,
    LocalDateTime timestamp,
    BigDecimal amount
){
}

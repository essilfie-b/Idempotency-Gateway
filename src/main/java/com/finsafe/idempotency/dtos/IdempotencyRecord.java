package com.finsafe.idempotency.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IdempotencyRecord {
    private String idempotencyKey;
    private Status status;
    private PaymentRequest requestBody;
    private PaymentResponse response;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}



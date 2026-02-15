package com.finsafe.idempotency.services;

import com.finsafe.idempotency.dtos.IdempotencyRecord;
import com.finsafe.idempotency.dtos.PaymentRequest;
import com.finsafe.idempotency.dtos.PaymentResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.finsafe.idempotency.dtos.Status.COMPLETED;
import static com.finsafe.idempotency.dtos.Status.IN_PROGRESS;

@Service
@AllArgsConstructor
public class PaymentService {

    private final IdempotencyService idempotencyService;

    public PaymentResponse processPayment(PaymentRequest paymentRequest, String idempotencyKey) throws InterruptedException {
        var response = createPaymentResponse(paymentRequest);
        var idempotencyRecord = createIdempotencyRecord(paymentRequest, idempotencyKey, response);

        idempotencyService.save(idempotencyRecord);

        Thread.sleep(2000);
        
        idempotencyRecord.setStatus(COMPLETED);
        idempotencyRecord.setCompletedAt(LocalDateTime.now());
        idempotencyService.update(idempotencyRecord);


        return response;
    }

    private PaymentResponse createPaymentResponse(PaymentRequest paymentRequest) {
        return PaymentResponse.builder()
                .message("Payment Successful")
                .status("success")
                .transactionId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .amount(paymentRequest.amount())
                .build();
    }

    private IdempotencyRecord createIdempotencyRecord(PaymentRequest paymentRequest, String idempotencyKey, PaymentResponse response) {
        return IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .requestBody(paymentRequest)
                .response(response)
                .status(IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

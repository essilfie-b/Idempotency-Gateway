package com.finsafe.idempotency.services;

import com.finsafe.idempotency.dtos.PaymentRequest;
import com.finsafe.idempotency.dtos.PaymentResponse;
import com.finsafe.idempotency.exceptions.PaymentProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.finsafe.idempotency.dtos.Status.COMPLETED;

@Service
@AllArgsConstructor
public class PaymentService {

    private final IdempotencyService idempotencyService;

    public PaymentResponse processPayment(PaymentRequest paymentRequest, String idempotencyKey) {
        PaymentResponse response = executePaymentProcessing(paymentRequest);

        var idempotencyRecord = idempotencyService.getByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new PaymentProcessingException("Idempotency record not found"));

        idempotencyRecord.setResponse(response);
        idempotencyRecord.setStatus(COMPLETED);
        idempotencyRecord.setCompletedAt(LocalDateTime.now());
        idempotencyService.update(idempotencyRecord);


        return response;
    }

    private PaymentResponse executePaymentProcessing(PaymentRequest paymentRequest) {
        try {
            // Simulate payment processing
            Thread.sleep(2000);

            return PaymentResponse.builder()
                    .message("Payment Successful")
                    .status("success")
                    .transactionId(UUID.randomUUID().toString())
                    .timestamp(LocalDateTime.now())
                    .amount(paymentRequest.amount())
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentProcessingException("Payment processing interrupted");
        }
    }
}

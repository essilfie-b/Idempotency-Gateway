package com.finsafe.idempotency;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    public PaymentResponse processPayment(@Valid PaymentRequest paymentRequest) throws InterruptedException {
        var response = PaymentResponse.builder()
                .message("Payment Successful")
                .status("success")
                .transactionId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .amount(paymentRequest.amount())
                .build();


        Thread.sleep(2000);


        return response;
    }

}

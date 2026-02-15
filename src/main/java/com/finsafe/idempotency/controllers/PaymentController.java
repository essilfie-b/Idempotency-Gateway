package com.finsafe.idempotency.controllers;


import com.finsafe.idempotency.services.PaymentService;
import com.finsafe.idempotency.dtos.PaymentRequest;
import com.finsafe.idempotency.dtos.PaymentResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process-payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Valid PaymentRequest paymentRequest,
            @RequestHeader("Idempotency-Key") String idempotencyKey) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(paymentRequest, idempotencyKey));
    }
}

package com.finsafe.idempotency;


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
            @RequestBody @Valid PaymentRequest paymentRequest) throws InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(paymentRequest));
    }
}

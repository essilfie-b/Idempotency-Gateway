package com.finsafe.idempotency.controllers;

import com.finsafe.idempotency.dtos.ErrorResponse;
import com.finsafe.idempotency.exceptions.IdempotencyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<String>>> handleValidationException(
            MethodArgumentNotValidException ex) {

        var fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse<>("Validation Failed", fieldErrors));
    }

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ErrorResponse<String>> handleIdempotencyKeyMismatch(
            IdempotencyException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse<>("Idempotency error", ex.getMessage()));
    }

}

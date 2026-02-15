package com.finsafe.idempotency;

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

        var errorResponse = new ErrorResponse<List<String>>();
        errorResponse.setMessage("Validation Failed");
        errorResponse.setError(ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}

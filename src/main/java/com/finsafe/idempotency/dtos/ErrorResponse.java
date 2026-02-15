package com.finsafe.idempotency.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse<T> {
    private String message;
    private T error;
}

package com.finsafe.idempotency.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ErrorResponse<T> {
    private String message;
    private T error;
}

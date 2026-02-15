package com.finsafe.idempotency.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finsafe.idempotency.dtos.IdempotencyRecord;
import com.finsafe.idempotency.dtos.Status;
import com.finsafe.idempotency.exceptions.IdempotencyException;
import com.finsafe.idempotency.services.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null) throw new IdempotencyException("Idempotency-Key header is null");

        var placeholderRecord = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .status(Status.IN_PROGRESS)
                .build();

        boolean reserved = idempotencyService.save(placeholderRecord);

        if (reserved)
            return true;
        else {
            var idempotencyRecord = idempotencyService.getByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new IdempotencyException("Idempotency record vanished unexpectedly"));

            if (idempotencyRecord.getStatus() == Status.COMPLETED) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(idempotencyRecord.getResponse()));
                return false;
            }

            throw new IdempotencyException("Request with this idempotency key is already being processed");
        }

    }
}

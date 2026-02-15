package com.finsafe.idempotency.services;

import com.finsafe.idempotency.dtos.IdempotencyRecord;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {
    private final Map<String, IdempotencyRecord> idempotencyRecords = new ConcurrentHashMap<>();


    public Optional<IdempotencyRecord> getByIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(idempotencyRecords.get(idempotencyKey));
    }

    public void save(IdempotencyRecord idempotencyRecord) {
        idempotencyRecords.putIfAbsent(idempotencyRecord.getIdempotencyKey(),  idempotencyRecord);
    }

    public void update(IdempotencyRecord idempotencyRecord) {
        idempotencyRecords.put(idempotencyRecord.getIdempotencyKey(), idempotencyRecord);
    }

}

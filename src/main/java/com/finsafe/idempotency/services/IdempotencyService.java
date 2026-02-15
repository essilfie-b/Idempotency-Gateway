package com.finsafe.idempotency.services;

import com.finsafe.idempotency.dtos.IdempotencyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class IdempotencyService {
    private final Map<String, IdempotencyRecord> idempotencyRecords = new ConcurrentHashMap<>();


    public Optional<IdempotencyRecord> getByIdempotencyKey(String idempotencyKey) {
        return Optional.ofNullable(idempotencyRecords.get(idempotencyKey));
    }

    /**
     * Atomically saves the idempotency record if not already present.
     * @return true if the record was saved (key was new), false if key already existed
     */
    public boolean save(IdempotencyRecord idempotencyRecord) {
        IdempotencyRecord existing = idempotencyRecords.putIfAbsent(
                idempotencyRecord.getIdempotencyKey(),
                idempotencyRecord
        );
        log.info("save idempotency record: {}", existing);
        return existing == null;
    }

    public void update(IdempotencyRecord idempotencyRecord) {
        idempotencyRecords.put(idempotencyRecord.getIdempotencyKey(), idempotencyRecord);
        log.info("update idempotency record: {}", idempotencyRecord);
    }

}

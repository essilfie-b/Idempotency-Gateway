package com.finsafe.idempotency.services;

import com.finsafe.idempotency.config.IdempotencyProperties;
import com.finsafe.idempotency.dtos.IdempotencyRecord;
import com.finsafe.idempotency.dtos.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@AllArgsConstructor
public class IdempotencyService {
    private final Map<String, IdempotencyRecord> idempotencyRecords = new ConcurrentHashMap<>();
    private final IdempotencyProperties properties;


    public Optional<IdempotencyRecord> getByIdempotencyKey(String idempotencyKey) {
        IdempotencyRecord idempotencyRecord = idempotencyRecords.get(idempotencyKey);

        if (idempotencyRecord != null && isExpired(idempotencyRecord)) {
            log.info("Record expired for key: {}, removing lazily", idempotencyKey);
            idempotencyRecords.remove(idempotencyKey);
            return Optional.empty();
        }

        return Optional.ofNullable(idempotencyRecord);
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

    public boolean isExpired(IdempotencyRecord idempotencyRecord) {
        LocalDateTime now = LocalDateTime.now();

        if (idempotencyRecord.getStatus() == Status.IN_PROGRESS) {
            if (idempotencyRecord.getCreatedAt() == null) return false;
            long minutesElapsed = java.time.Duration.between(idempotencyRecord.getCreatedAt(), now).toMinutes();
            return minutesElapsed > properties.getTtl().getInProgressMinutes();
        }

        if (idempotencyRecord.getStatus() == Status.COMPLETED || idempotencyRecord.getStatus() == Status.FAILED) {
            if (idempotencyRecord.getCompletedAt() == null) return false;
            long secondsElapsed = java.time.Duration.between(idempotencyRecord.getCompletedAt(), now).toSeconds();
            return secondsElapsed > properties.getTtl().getCompletedRecordsSeconds();
        }

        return false;
    }

    public int removeExpiredRecords() {
        int removed = 0;

        for (Map.Entry<String, IdempotencyRecord> entry : idempotencyRecords.entrySet()) {
            if (isExpired(entry.getValue())) {
                idempotencyRecords.remove(entry.getKey());
                removed++;
            }
        }

        if (removed > 0) {
            log.info("Removed {} expired idempotency records", removed);
        }

        return removed;
    }

    public int getRecordCount() {
        return idempotencyRecords.size();
    }

}

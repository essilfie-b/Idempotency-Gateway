package com.finsafe.idempotency.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class IdempotencyCleanupService {

    private final IdempotencyService idempotencyService;

    @Scheduled(cron = "${idempotency.cleanup.schedule-cron}")
    public void cleanupExpiredRecords() {
        log.debug("Starting scheduled cleanup of expired idempotency records");

        int recordCountBefore = idempotencyService.getRecordCount();
        int removedCount = idempotencyService.removeExpiredRecords();
        int recordCountAfter = idempotencyService.getRecordCount();

        if (removedCount > 0) {
            log.info("Cleanup completed: removed {} records (before: {}, after: {})",
                    removedCount, recordCountBefore, recordCountAfter);
        } else {
            log.debug("Cleanup completed: no expired records found (total: {})", recordCountAfter);
        }
    }
}

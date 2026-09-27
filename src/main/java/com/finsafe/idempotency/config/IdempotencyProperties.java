package com.finsafe.idempotency.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "idempotency")
@Getter
@Setter
public class IdempotencyProperties {

    private Ttl ttl = new Ttl();
    private Cleanup cleanup = new Cleanup();

    @Getter
    @Setter
    public static class Ttl {
        private long completedRecordsSeconds = 86400; // 24 hours default
        private long inProgressMinutes = 10;
    }

    @Getter
    @Setter
    public static class Cleanup {
        private String scheduleCron = "0 */5 * * * *";
    }
}

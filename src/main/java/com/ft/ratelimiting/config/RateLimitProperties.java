package com.ft.ratelimiting.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ratelimit")
@Getter
@Setter
public class RateLimitProperties {

    private Bucket token;
    private Bucket leaky;

    @Getter @Setter
    public static class Bucket {
        private int capacity;
        private int refillRate;
    }
}

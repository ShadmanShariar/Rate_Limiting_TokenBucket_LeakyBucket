package com.ft.ratelimiting.service;

import com.ft.ratelimiting.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeakyBucketService {

    private final StringRedisTemplate redis;
    private final RateLimitProperties props;

    public boolean allowRequest(String userId) {
        String key = "leaky:" + userId;

        int capacity = props.getLeaky().getCapacity();
        int leakRate = props.getLeaky().getRefillRate();

        long now = System.currentTimeMillis();

        List<Object> values = redis.opsForHash()
                .multiGet(key, List.of("water", "lastLeak"));

        double water = values.get(0) == null ? 0 : Double.parseDouble(values.get(0).toString());
        long lastLeak = values.get(1) == null ? now : Long.parseLong(values.get(1).toString());

        double minutes = (now - lastLeak) / 60000.0;
        water = Math.max(0, water - minutes * leakRate);

        if (water + 1 > capacity) return false;

        water++;

        redis.opsForHash().put(key, "water", String.valueOf(water));
        redis.opsForHash().put(key, "lastLeak", String.valueOf(now));
        redis.expire(key, Duration.ofMinutes(10));

        return true;
    }
}
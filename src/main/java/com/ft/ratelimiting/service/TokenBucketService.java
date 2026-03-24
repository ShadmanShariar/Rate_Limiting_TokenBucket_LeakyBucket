package com.ft.ratelimiting.service;

import com.ft.ratelimiting.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenBucketService {

    private final StringRedisTemplate redis;
    private final RateLimitProperties props;

    public boolean allowRequest(String key) {
        String redisKey = "token:" + key;

        int capacity = props.getToken().getCapacity();
        int refillRate = props.getToken().getRefillRate();

        long now = System.currentTimeMillis();

        List<Object> values = redis.opsForHash()
                .multiGet(redisKey, List.of("tokens", "lastRefill"));

        double tokens = values.get(0) == null ? capacity : Double.parseDouble(values.get(0).toString());
        long lastRefill = values.get(1) == null ? now : Long.parseLong(values.get(1).toString());

        double secondsPassed = (now - lastRefill) / 1000.0;
        tokens = Math.min(capacity, tokens + secondsPassed * refillRate);

        if (tokens < 1) {
            return false;
        }

        tokens -= 1;

        redis.opsForHash().put(redisKey, "tokens", String.valueOf(tokens));
        redis.opsForHash().put(redisKey, "lastRefill", String.valueOf(now));
        redis.expire(redisKey, Duration.ofMinutes(10));

        return true;
    }
}
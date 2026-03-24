package com.ft.ratelimiting.controller;

import com.ft.ratelimiting.service.LeakyBucketService;
import com.ft.ratelimiting.service.TokenBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final TokenBucketService tokenService;
    private final LeakyBucketService leakyService;

    // Example: curl localhost:8080/api/token?userId=123
    @GetMapping("/token")
    public String tokenApi(@RequestParam String userId) {
        if (!tokenService.allowRequest(userId)) {
            return "Token limit exceeded";
        }
        return "Token API success";
    }

    // Example: curl localhost:8080/api/leaky?userId=123
    @GetMapping("/leaky")
    public String leakyApi(@RequestParam String userId) {
        if (!leakyService.allowRequest(userId)) {
            return "Leaky limit exceeded";
        }
        return "Leaky API success";
    }
}

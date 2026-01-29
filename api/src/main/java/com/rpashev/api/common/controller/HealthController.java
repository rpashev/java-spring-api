package com.rpashev.api.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String health() {
        log.info("Health check endpoint called");
        return "OK";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "You are authenticated!";
    }
}

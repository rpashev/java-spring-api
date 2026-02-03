package com.rpashev.api.common.controller;

import com.rpashev.api.auth.dto.AuthResponseDTO;
import com.rpashev.api.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    @Tag(name = "Health checks")
    @Operation(summary = "Checks public endpoint")
    @GetMapping
    public String health() {
        log.info("Health check public endpoint called");
        return "OK";
    }

    @Tag(name = "Health checks")
    @Operation(summary = "Checks protected endpoint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/protected")
    public String protectedEndpoint() {
        log.info("Health check protected endpoint called");
        return "You are authenticated!";
    }
}

package com.rpashev.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private int status;
    private String error;
    private String message;
    private String path;
}

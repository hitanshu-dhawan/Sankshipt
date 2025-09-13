package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for creating short URL requests.
 * Used specifically for POST /api/urls endpoint.
 */
@Data
@Schema(description = "Request object for creating a new short URL")
public class CreateShortUrlRequest {

    /**
     * The original long URL to be shortened.
     * Required for creating a new short URL.
     * Should be a valid URL (e.g., "https://www.example.com/very/long/path").
     */
    @NotNull(message = "Original URL is required")
    @NotBlank(message = "Original URL cannot be blank")
    @Schema(
            description = "The original long URL to be shortened",
            example = "https://www.example.com/very/long/path/to/some/resource",
            required = true
    )
    private String originalUrl;

}

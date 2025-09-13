package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for Click API responses.
 * Used to return click information to the client without exposing internal database structure.
 * <p>
 * This response is returned by:
 * - GET /api/analytics/{shortCode}/clicks (when retrieving click analytics)
 */
@Data
@Schema(description = "Response object containing click analytics information")
public class ClickResponse {

    /**
     * Unique identifier for the click record.
     */
    @Schema(description = "Unique identifier for the click record", example = "1")
    private Long id;

    /**
     * The short code that was clicked.
     */
    @Schema(description = "The short code that was clicked", example = "abc123")
    private String shortCode;

    /**
     * The original URL that the short code redirects to.
     */
    @Schema(
            description = "The original URL that the short code redirects to",
            example = "https://www.example.com/very/long/path/to/some/resource"
    )
    private String originalUrl;

    /**
     * Timestamp when the click occurred.
     */
    @Schema(description = "Timestamp when the click occurred", example = "2025-09-10 16:16:06.366000")
    private Date clickedAt;

    /**
     * User agent string of the browser/client that made the click.
     */
    @Schema(
            description = "User agent string of the browser/client that made the click",
            example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36"
    )
    private String userAgent;

}

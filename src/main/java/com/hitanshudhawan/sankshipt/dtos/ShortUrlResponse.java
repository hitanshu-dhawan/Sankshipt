package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for Short URL API responses.
 * Used to return information about short URL operations to the client.
 * <p>
 * This response is returned by:
 * - POST /api/urls (when creating a new short URL)
 * - DELETE /api/urls (when deleting an existing short URL)
 */
@Data
@Schema(description = "Response object for short URL operations")
public class ShortUrlResponse {

    /**
     * The original long URL that was shortened.
     * This is the full URL that the short code redirects to.
     */
    @Schema(
            description = "The original long URL that was shortened",
            example = "https://www.example.com/very/long/path/to/some/resource"
    )
    private String originalUrl;

    /**
     * The generated short code that represents the original URL.
     * This code can be appended to the base URL to create the shortened link.
     */
    @Schema(
            description = "The generated short code that represents the original URL",
            example = "1facb5c"
    )
    private String shortCode;

}

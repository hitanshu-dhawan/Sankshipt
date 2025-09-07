package com.hitanshudhawan.sankshipt.dtos;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for Short URL API requests.
 * Used to receive data from clients for short URL operations.
 * <p>
 * This request is used by:
 * - POST /api/urls (when creating a new short URL - requires originalUrl)
 * - DELETE /api/urls (when deleting an existing short URL - requires shortCode)
 */
@Data
public class ShortUrlRequest {

    /**
     * The original long URL to be shortened.
     * Required for POST requests when creating a new short URL.
     * Should be a valid URL (e.g., "https://www.example.com/very/long/path").
     */
    private String originalUrl;

    /**
     * The short code of an existing URL mapping.
     * Required for DELETE requests when deleting a short URL.
     * This is the generated code that identifies the URL to be deleted.
     */
    private String shortCode;

}

package com.hitanshudhawan.sankshipt.dtos;

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
public class ShortUrlResponse {

    /**
     * The original long URL that was shortened.
     * This is the full URL that the short code redirects to.
     */
    private String originalUrl;

    /**
     * The generated short code that represents the original URL.
     * This code can be appended to the base URL to create the shortened link.
     */
    private String shortCode;

}

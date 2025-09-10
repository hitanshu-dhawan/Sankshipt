package com.hitanshudhawan.sankshipt.dtos;

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
public class ClickResponse {

    /**
     * Unique identifier for the click record.
     */
    private Long id;

    /**
     * The short code that was clicked.
     */
    private String shortCode;

    /**
     * The original URL that the short code redirects to.
     */
    private String originalUrl;

    /**
     * Timestamp when the click occurred.
     */
    private Date clickedAt;

    /**
     * User agent string of the browser/client that made the click.
     */
    private String userAgent;

}

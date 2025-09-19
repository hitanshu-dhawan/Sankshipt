package com.hitanshudhawan.sankshipt.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

/**
 * Entity representing an authentication token.
 * Used for user session management and API authentication.
 */
@Entity(name = "tokens")
@Data
@Schema(description = "Authentication token information")
public class Token extends BaseModel {

    /**
     * The actual token value used for authentication.
     * This is typically a JWT or similar token format.
     */
    @Schema(
            description = "The authentication token value",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String value;

    /**
     * The user this token belongs to.
     * Hidden from API responses for security.
     */
    @ManyToOne
    @Schema(hidden = true)
    private User user;

    /**
     * When this token expires.
     * After this date, the token will no longer be valid.
     */
    @Schema(
            description = "Token expiration date",
            example = "2025-09-17T10:30:00.000Z"
    )
    private Date expiryDate;

    /**
     * Whether this token has been manually expired/invalidated.
     * Used for logout functionality.
     */
    @Schema(
            description = "Whether the token has been manually expired",
            example = "false"
    )
    private boolean isExpired;

}

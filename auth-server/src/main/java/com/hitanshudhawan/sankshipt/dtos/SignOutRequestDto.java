package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for user sign-out requests.
 * Used specifically for POST /api/users/signout endpoint.
 */
@Data
@Schema(description = "Request object for user sign-out")
public class SignOutRequestDto {

    /**
     * The authentication token to be invalidated.
     * This token was previously issued during the sign-in process.
     */
    @NotNull(message = "Token is required")
    @NotBlank(message = "Token cannot be blank")
    @Schema(
            description = "The authentication token to be invalidated",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true
    )
    private String token;

}

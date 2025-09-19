package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for user sign-in requests.
 * Used specifically for POST /api/users/signin endpoint.
 */
@Data
@Schema(description = "Request object for user authentication")
public class SignInRequestDto {

    /**
     * User's email address used for authentication.
     * Must be a valid email format and correspond to an existing user account.
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Schema(
            description = "User's email address for authentication",
            example = "user@example.com",
            required = true
    )
    private String email;

    /**
     * User's password for authentication.
     * Should match the password associated with the provided email.
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Schema(
            description = "User's password for authentication",
            example = "mySecurePassword123",
            required = true
    )
    private String password;

}

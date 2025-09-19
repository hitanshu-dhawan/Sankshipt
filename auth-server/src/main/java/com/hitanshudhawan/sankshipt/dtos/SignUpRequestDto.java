package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * Used specifically for POST /api/users/signup endpoint.
 */
@Data
@Schema(description = "Request object for user registration")
public class SignUpRequestDto {

    /**
     * User's first name.
     * Required for creating a new user account.
     */
    @NotNull(message = "First name is required")
    @NotBlank(message = "First name cannot be blank")
    @Schema(
            description = "User's first name",
            example = "John",
            required = true
    )
    private String firstName;

    /**
     * User's last name.
     * Required for creating a new user account.
     */
    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name cannot be blank")
    @Schema(
            description = "User's last name",
            example = "Doe",
            required = true
    )
    private String lastName;

    /**
     * User's email address.
     * Must be unique and will be used for authentication.
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Schema(
            description = "User's email address (must be unique)",
            example = "john.doe@example.com",
            required = true
    )
    private String email;

    /**
     * User's password.
     * Should be strong and secure for account protection.
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Schema(
            description = "User's password",
            example = "mySecurePassword123",
            required = true
    )
    private String password;

    /**
     * List of roles to assign to the user.
     * Determines the user's permissions within the system.
     */
    @Schema(
            description = "List of roles to assign to the user",
            example = "[\"USER\", \"ADMIN\"]"
    )
    private List<String> roles;

}

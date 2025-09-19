package com.hitanshudhawan.sankshipt.dtos;

import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) for User API responses.
 * Used to return user information to the client without exposing sensitive data.
 * <p>
 * This response is returned by:
 * - POST /api/users/signup (when creating a new user)
 * - GET /api/users/validate/{token} (when validating a token)
 */
@Data
@Schema(description = "Response object containing user information")
public class UserDto {

    /**
     * User's first name.
     */
    @Schema(
            description = "User's first name",
            example = "John"
    )
    private String firstName;

    /**
     * User's last name.
     */
    @Schema(
            description = "User's last name",
            example = "Doe"
    )
    private String lastName;

    /**
     * User's email address.
     */
    @Schema(
            description = "User's email address",
            example = "john.doe@example.com"
    )
    private String email;

    /**
     * List of roles assigned to the user.
     * Determines the user's permissions within the system.
     */
    @Schema(
            description = "List of roles assigned to the user",
            example = "[{\"id\": 1, \"name\": \"USER\"}, {\"id\": 2, \"name\": \"ADMIN\"}]"
    )
    private List<Role> roles;


    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

}

package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.SignInRequestDto;
import com.hitanshudhawan.sankshipt.dtos.SignOutRequestDto;
import com.hitanshudhawan.sankshipt.dtos.SignUpRequestDto;
import com.hitanshudhawan.sankshipt.dtos.UserDto;
import com.hitanshudhawan.sankshipt.exceptions.TokenNotFoundException;
import com.hitanshudhawan.sankshipt.exceptions.UserAlreadyExistsException;
import com.hitanshudhawan.sankshipt.exceptions.UserNotFoundException;
import com.hitanshudhawan.sankshipt.models.Token;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "API for user authentication and management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @Operation(
            operationId = "01_signUp",
            summary = "Register a new user",
            description = "Creates a new user account with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - User already exists",
                    content = @Content
            )
    })
    public ResponseEntity<UserDto> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignUpRequestDto.class))
            )
            @RequestBody @Valid SignUpRequestDto signUpRequestDto
    ) throws UserAlreadyExistsException {
        User user = userService.signUp(
                signUpRequestDto.getFirstName(),
                signUpRequestDto.getLastName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getRoles()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserDto.fromUser(user));
    }

    @PostMapping("/signin")
    @Operation(
            operationId = "02_signIn",
            summary = "Sign in user",
            description = "Authenticates user credentials and returns an access token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User signed in successfully",
                    content = @Content(schema = @Schema(implementation = Token.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid credentials",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    public ResponseEntity<Token> signIn(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User sign-in credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignInRequestDto.class))
            )
            @RequestBody @Valid SignInRequestDto signInRequestDto
    ) throws UserNotFoundException {
        Token token = userService.signIn(
                signInRequestDto.getEmail(),
                signInRequestDto.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

    @PostMapping("/signout")
    @Operation(
            operationId = "03_signOut",
            summary = "Sign out user",
            description = "Invalidates the user's access token and signs them out"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User signed out successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> signOut(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Token to be invalidated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignOutRequestDto.class))
            )
            @RequestBody @Valid SignOutRequestDto signOutRequestDto
    ) throws TokenNotFoundException {
        userService.signOut(signOutRequestDto.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/validate/{token}")
    @Operation(
            operationId = "04_validateToken",
            summary = "Validate user token",
            description = "Validates the provided token and returns user information if valid"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token not found or invalid",
                    content = @Content
            )
    })
    public ResponseEntity<UserDto> validateToken(
            @Parameter(description = "The token to validate", required = true)
            @PathVariable String token
    ) throws TokenNotFoundException {
        User user = userService.validateToken(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserDto.fromUser(user));
    }

}

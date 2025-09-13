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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) throws UserAlreadyExistsException {
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
    public ResponseEntity<Token> signIn(@RequestBody SignInRequestDto signInRequestDto) throws UserNotFoundException {
        Token token = userService.signIn(
                signInRequestDto.getEmail(),
                signInRequestDto.getPassword()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(@RequestBody SignOutRequestDto signOutRequestDto) throws TokenNotFoundException {
        userService.signOut(signOutRequestDto.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<UserDto> validateToken(@PathVariable String token) throws TokenNotFoundException {
        User user = userService.validateToken(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(UserDto.fromUser(user));
    }

}

package com.hitanshudhawan.sankshipt.exceptionhandlers;

import com.hitanshudhawan.sankshipt.dtos.exceptions.TokenNotFoundExceptionDto;
import com.hitanshudhawan.sankshipt.dtos.exceptions.UserAlreadyExistsExceptionDto;
import com.hitanshudhawan.sankshipt.dtos.exceptions.UserNotFoundExceptionDto;
import com.hitanshudhawan.sankshipt.exceptions.TokenNotFoundException;
import com.hitanshudhawan.sankshipt.exceptions.UserAlreadyExistsException;
import com.hitanshudhawan.sankshipt.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserAlreadyExistsExceptionDto> handleUserAlreadyExistsException(UserAlreadyExistsException userAlreadyExistsException) {
        UserAlreadyExistsExceptionDto userAlreadyExistsExceptionDto = new UserAlreadyExistsExceptionDto();
        userAlreadyExistsExceptionDto.setEmail(userAlreadyExistsException.getEmail());
        userAlreadyExistsExceptionDto.setMessage(userAlreadyExistsException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(userAlreadyExistsExceptionDto);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundExceptionDto> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        UserNotFoundExceptionDto userNotFoundExceptionDto = new UserNotFoundExceptionDto();
        userNotFoundExceptionDto.setEmail(userNotFoundException.getEmail());
        userNotFoundExceptionDto.setMessage(userNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userNotFoundExceptionDto);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<TokenNotFoundExceptionDto> handleTokenNotFoundException(TokenNotFoundException tokenNotFoundException) {
        TokenNotFoundExceptionDto tokenNotFoundExceptionDto = new TokenNotFoundExceptionDto();
        tokenNotFoundExceptionDto.setToken(tokenNotFoundException.getToken());
        tokenNotFoundExceptionDto.setMessage(tokenNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tokenNotFoundExceptionDto);
    }

}

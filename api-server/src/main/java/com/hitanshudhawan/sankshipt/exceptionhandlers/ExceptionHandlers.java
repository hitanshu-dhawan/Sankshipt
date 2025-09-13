package com.hitanshudhawan.sankshipt.exceptionhandlers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(UrlNotFoundException urlNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(urlNotFoundException.getMessage());
    }

}

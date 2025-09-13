package com.hitanshudhawan.sankshipt.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAlreadyExistsException extends Exception {

    private String email;
    private String message;

    public UserAlreadyExistsException(String email, String message) {
        this.email = email;
        this.message = message;
    }

}

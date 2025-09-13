package com.hitanshudhawan.sankshipt.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenNotFoundException extends Exception {

    private String token;
    private String message;

    public TokenNotFoundException(String token, String message) {
        this.token = token;
        this.message = message;
    }

}

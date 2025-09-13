package com.hitanshudhawan.sankshipt.dtos.exceptions;

import lombok.Data;

@Data
public class TokenNotFoundExceptionDto {
    private String token;
    private String message;
}

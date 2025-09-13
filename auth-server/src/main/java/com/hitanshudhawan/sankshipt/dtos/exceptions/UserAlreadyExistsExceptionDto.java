package com.hitanshudhawan.sankshipt.dtos.exceptions;

import lombok.Data;

@Data
public class UserAlreadyExistsExceptionDto {
    private String email;
    private String message;
}

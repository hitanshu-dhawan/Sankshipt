package com.hitanshudhawan.sankshipt.dtos.exceptions;

import lombok.Data;

@Data
public class UserNotFoundExceptionDto {
    private String email;
    private String message;
}

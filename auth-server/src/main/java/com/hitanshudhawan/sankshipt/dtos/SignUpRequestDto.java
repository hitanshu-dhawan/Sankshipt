package com.hitanshudhawan.sankshipt.dtos;

import lombok.Data;

@Data
public class SignUpRequestDto {

    private String firstName;
    private String lastName;

    private String email;
    private String password;

}

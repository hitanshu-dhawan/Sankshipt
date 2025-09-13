package com.hitanshudhawan.sankshipt.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SignUpRequestDto {

    private String firstName;
    private String lastName;

    private String email;
    private String password;

    private List<String> roles;

}

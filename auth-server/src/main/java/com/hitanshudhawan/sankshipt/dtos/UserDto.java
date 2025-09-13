package com.hitanshudhawan.sankshipt.dtos;

import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String firstName;
    private String lastName;

    private String email;

    private List<Role> roles;


    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

}

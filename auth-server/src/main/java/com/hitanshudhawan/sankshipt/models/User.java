package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "users")
@Data
public class User extends BaseModel {

    private String firstName;
    private String lastName;

    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

}

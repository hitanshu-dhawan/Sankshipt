package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "users")
@Data
public class User extends BaseModel {

    private String email;

    @OneToMany(mappedBy = "user")
    private List<URL> urls;

}

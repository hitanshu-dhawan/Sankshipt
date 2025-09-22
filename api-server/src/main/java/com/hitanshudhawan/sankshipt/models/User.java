package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true)
})
@Data
public class User extends BaseModel {

    private String email;

    @OneToMany(mappedBy = "user")
    private List<URL> urls;

}

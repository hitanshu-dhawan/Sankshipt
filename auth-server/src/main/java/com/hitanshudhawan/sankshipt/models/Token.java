package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Entity(name = "tokens")
@Data
public class Token extends BaseModel {

    private String value;

    @ManyToOne
    private User user;

    private Date expiryDate;

    private boolean isExpired;

}

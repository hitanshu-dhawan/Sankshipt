package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity(name = "urls")
@Data
public class URL extends BaseModel {

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

}

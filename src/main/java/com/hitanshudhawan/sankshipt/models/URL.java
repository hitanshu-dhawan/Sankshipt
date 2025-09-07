package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "urls")
@Data
public class URL extends BaseModel {

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL)
    private List<Click> clicks;

}

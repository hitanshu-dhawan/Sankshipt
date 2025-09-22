package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "short_code", unique = true),
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
public class URL extends BaseModel {

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "url", cascade = CascadeType.ALL)
    private List<Click> clicks;

}

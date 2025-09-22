package com.hitanshudhawan.sankshipt.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "clicks", indexes = {
    @Index(name = "idx_url", columnList = "url")
})
@Data
public class Click extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "url", nullable = false)
    private URL url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "clicked_at")
    private Date clickedAt;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

}

package com.hitanshudhawan.sankshipt.dtos;

import lombok.Data;

@Data
public class ShortUrlResponse {
    private String originalUrl;
    private String shortCode;
}

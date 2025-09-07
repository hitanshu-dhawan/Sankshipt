package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.ShortUrlRequest;
import com.hitanshudhawan.sankshipt.dtos.ShortUrlResponse;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(@RequestBody ShortUrlRequest shortUrlRequest) {
        String originalUrl = shortUrlRequest.getOriginalUrl();
        URL createdUrl = shortUrlService.createShortUrl(originalUrl);

        ShortUrlResponse response = new ShortUrlResponse();
        response.setOriginalUrl(createdUrl.getOriginalUrl());
        response.setShortCode(createdUrl.getShortCode());

        return ResponseEntity.ok(response);
    }

}

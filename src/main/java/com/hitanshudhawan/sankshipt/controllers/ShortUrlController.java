package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.ShortUrlRequest;
import com.hitanshudhawan.sankshipt.dtos.ShortUrlResponse;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        
        // Create a new short URL mapping
        URL createdUrl = shortUrlService.createShortUrl(originalUrl);

        // Prepare response with the created URL information
        ShortUrlResponse response = new ShortUrlResponse();
        response.setOriginalUrl(createdUrl.getOriginalUrl());
        response.setShortCode(createdUrl.getShortCode());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ShortUrlResponse> deleteShortUrl(@RequestBody ShortUrlRequest shortUrlRequest) throws UrlNotFoundException {
        String shortCode = shortUrlRequest.getShortCode();

        // First, get the URL details before deletion for the response
        URL urlToDelete = shortUrlService.resolveShortCode(shortCode);

        // Delete the URL
        shortUrlService.deleteShortUrl(shortCode);

        // Prepare response with the deleted URL information
        ShortUrlResponse response = new ShortUrlResponse();
        response.setOriginalUrl(urlToDelete.getOriginalUrl());
        response.setShortCode(urlToDelete.getShortCode());

        return ResponseEntity.ok(response);
    }

}

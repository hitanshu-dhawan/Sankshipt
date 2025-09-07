package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final ShortUrlService shortUrlService;
    private final ClickAnalyticsService clickAnalyticsService;

    public AnalyticsController(
            ShortUrlService shortUrlService,
            ClickAnalyticsService clickAnalyticsService
    ) {
        this.shortUrlService = shortUrlService;
        this.clickAnalyticsService = clickAnalyticsService;
    }

    @GetMapping("/{shortCode}/count")
    public ResponseEntity<Long> getShortUrlClickCount(@PathVariable String shortCode) throws UrlNotFoundException {
        URL url = shortUrlService.resolveShortCode(shortCode);
        Long clickCount = clickAnalyticsService.getClickCountForUrl(url);
        return ResponseEntity.ok(clickCount);
    }

//    @GetMapping("/{shortCode}/clicks")
//    public ResponseEntity<List<Response>> getShortUrlClicks(@PathVariable String shortCode) throws UrlNotFoundException {
//        URL url = shortUrlService.resolveShortCode(shortCode);
//
//        // TODO
//    }

}

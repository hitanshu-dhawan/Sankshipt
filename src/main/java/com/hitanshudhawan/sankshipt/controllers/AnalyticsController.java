package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.ClickResponse;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{shortCode}/clicks")
    public ResponseEntity<Page<ClickResponse>> getShortUrlClicks(
            @PathVariable String shortCode,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortOrder", required = false) String sortOrder
    ) throws UrlNotFoundException {
        URL url = shortUrlService.resolveShortCode(shortCode);
        Page<Click> clicks = clickAnalyticsService.getClicksForUrl(url, pageNumber, pageSize, sortOrder);
        return ResponseEntity.ok(clicks.map(this::convertToClickResponse));
    }

    /**
     * Converts a Click entity to a ClickResponse DTO.
     *
     * @param click the Click entity to convert
     * @return the converted ClickResponse DTO
     */
    private ClickResponse convertToClickResponse(Click click) {
        ClickResponse response = new ClickResponse();
        response.setId(click.getId());
        response.setShortCode(click.getUrl().getShortCode());
        response.setOriginalUrl(click.getUrl().getOriginalUrl());
        response.setClickedAt(click.getClickedAt());
        response.setUserAgent(click.getUserAgent());
        return response;
    }

}

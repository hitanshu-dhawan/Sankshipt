package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UrlController {

    private final ShortUrlService shortUrlService;
    private final ClickAnalyticsService clickAnalyticsService;

    public UrlController(
            ShortUrlService shortUrlService,
            ClickAnalyticsService clickAnalyticsService
    ) {
        this.shortUrlService = shortUrlService;
        this.clickAnalyticsService = clickAnalyticsService;
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirectToUrl(@PathVariable String shortCode, HttpServletRequest request) throws UrlNotFoundException {
        URL url = shortUrlService.resolveShortCode(shortCode);

        // Record the click for analytics
        try {
            clickAnalyticsService.recordClick(url, request);
        } catch (Exception e) {
            // Don't fail the redirect if analytics recording fails
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        redirectView.setUrl(url.getOriginalUrl());
        return redirectView;
    }

}

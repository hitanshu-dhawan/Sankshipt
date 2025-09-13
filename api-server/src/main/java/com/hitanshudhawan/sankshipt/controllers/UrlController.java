package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Tag(name = "2. URL Redirection", description = "API for redirecting short URLs to original URLs")
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
    @Operation(
            operationId = "redirectToUrl",
            summary = "Redirect to original URL",
            description = "Redirects to the original URL associated with the given short code and records the click for analytics"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "301",
                    description = "Permanent redirect to the original URL",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content
            )
    })
    public RedirectView redirectToUrl(
            @Parameter(description = "The short code to redirect", required = true)
            @PathVariable String shortCode,
            HttpServletRequest request
    ) throws UrlNotFoundException {
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

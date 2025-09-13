package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.ClickResponse;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "3. Analytics", description = "Analytics API for URL click tracking and statistics")
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
    @Operation(
            summary = "Get click count for a short URL",
            description = "Returns the total number of clicks for a given short code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Click count retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content
            )
    })
    public ResponseEntity<Long> getShortUrlClickCount(
            @Parameter(description = "The short code to get click count for", required = true)
            @PathVariable String shortCode) throws UrlNotFoundException {
        URL url = shortUrlService.resolveShortCode(shortCode);
        Long clickCount = clickAnalyticsService.getClickCountForUrl(url);
        return ResponseEntity.ok(clickCount);
    }

    @GetMapping("/{shortCode}/clicks")
    @Operation(
            summary = "Get paginated clicks for a short URL",
            description = "Returns a paginated list of all clicks for a given short code with optional sorting"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Clicks retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content
            )
    })
    public ResponseEntity<Page<ClickResponse>> getShortUrlClicks(
            @Parameter(description = "The short code to get clicks for", required = true)
            @PathVariable String shortCode,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @Parameter(description = "Number of items per page")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Parameter(description = "Sort order (asc/desc)")
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

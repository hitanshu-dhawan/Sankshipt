package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.dtos.CreateShortUrlRequest;
import com.hitanshudhawan.sankshipt.dtos.DeleteShortUrlRequest;
import com.hitanshudhawan.sankshipt.dtos.ShortUrlResponse;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.AuthenticationService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@Tag(name = "1. URL Management", description = "API for creating and managing short URLs")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final AuthenticationService authenticationService;

    public ShortUrlController(
            ShortUrlService shortUrlService,
            AuthenticationService authenticationService
    ) {
        this.shortUrlService = shortUrlService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @Operation(
            operationId = "01_createShortUrl",
            summary = "Create a new short URL",
            description = "Creates a short URL mapping for the provided original URL. The URL will be owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Short URL created successfully",
                    content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('SCOPE_api.write')")
    public ResponseEntity<ShortUrlResponse> createShortUrl(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the original URL to be shortened",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateShortUrlRequest.class))
            )
            @RequestBody @Valid CreateShortUrlRequest createShortUrlRequest
    ) {
        String originalUrl = createShortUrlRequest.getOriginalUrl();
        User currentUser = authenticationService.getCurrentUser();

        // Create a new short URL mapping with user ownership
        URL createdUrl = shortUrlService.createShortUrl(originalUrl, currentUser);

        // Prepare response with the created URL information
        ShortUrlResponse response = new ShortUrlResponse();
        response.setOriginalUrl(createdUrl.getOriginalUrl());
        response.setShortCode(createdUrl.getShortCode());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            operationId = "02_deleteShortUrl",
            summary = "Delete a short URL",
            description = "Deletes an existing short URL mapping using the short code. Users can only delete URLs they own."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Short URL deleted successfully",
                    content = @Content(schema = @Schema(implementation = ShortUrlResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User does not own this URL",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found",
                    content = @Content
            )
    })
    @PreAuthorize("hasAuthority('SCOPE_api.delete')")
    public ResponseEntity<ShortUrlResponse> deleteShortUrl(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request containing the short code to be deleted",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeleteShortUrlRequest.class))
            )
            @RequestBody @Valid DeleteShortUrlRequest deleteShortUrlRequest
    ) throws UrlNotFoundException {
        String shortCode = deleteShortUrlRequest.getShortCode();
        User currentUser = authenticationService.getCurrentUser();

        // First, get the URL details before deletion for the response
        URL urlToDelete = shortUrlService.resolveShortCode(shortCode);

        // Check if the current user owns this URL
        if (!shortUrlService.isUrlOwner(shortCode, currentUser)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Delete the URL (this will verify ownership again)
        shortUrlService.deleteShortUrl(shortCode, currentUser);

        // Prepare response with the deleted URL information
        ShortUrlResponse response = new ShortUrlResponse();
        response.setOriginalUrl(urlToDelete.getOriginalUrl());
        response.setShortCode(urlToDelete.getShortCode());

        return ResponseEntity.ok(response);
    }

}

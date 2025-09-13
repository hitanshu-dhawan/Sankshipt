package com.hitanshudhawan.sankshipt.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for deleting short URL requests.
 * Used specifically for DELETE /api/urls endpoint.
 */
@Data
@Schema(description = "Request object for deleting an existing short URL")
public class DeleteShortUrlRequest {

    /**
     * The short code of an existing URL mapping.
     * Required for deleting a short URL.
     * This is the generated code that identifies the URL to be deleted.
     */
    @NotNull(message = "Short code is required")
    @NotBlank(message = "Short code cannot be blank")
    @Schema(
            description = "The short code of an existing URL mapping",
            example = "1facb5c",
            required = true
    )
    private String shortCode;

}

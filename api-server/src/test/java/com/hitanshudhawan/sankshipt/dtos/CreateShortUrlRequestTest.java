package com.hitanshudhawan.sankshipt.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateShortUrlRequestTest {

    private Validator validator;
    private CreateShortUrlRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new CreateShortUrlRequest();
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setOriginalUrl("https://www.example.com");

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullOriginalUrl_ShouldFailValidation() {
        // Arrange
        request.setOriginalUrl(null);

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // Both @NotNull and @NotBlank trigger for null values
    }

    @Test
    void emptyOriginalUrl_ShouldFailValidation() {
        // Arrange
        request.setOriginalUrl("");

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<CreateShortUrlRequest> violation = violations.iterator().next();
        assertEquals("Original URL cannot be blank", violation.getMessage());
        assertEquals("originalUrl", violation.getPropertyPath().toString());
    }

    @Test
    void blankOriginalUrl_ShouldFailValidation() {
        // Arrange
        request.setOriginalUrl("   ");

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<CreateShortUrlRequest> violation = violations.iterator().next();
        assertEquals("Original URL cannot be blank", violation.getMessage());
        assertEquals("originalUrl", violation.getPropertyPath().toString());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String testUrl = "https://test.example.com";

        // Act
        request.setOriginalUrl(testUrl);

        // Assert
        assertEquals(testUrl, request.getOriginalUrl());
    }

    @Test
    void longUrl_ShouldPassValidation() {
        // Arrange
        String longUrl = "https://www.example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2&param3=value3";
        request.setOriginalUrl(longUrl);

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(longUrl, request.getOriginalUrl());
    }

    @Test
    void httpUrl_ShouldPassValidation() {
        // Arrange
        request.setOriginalUrl("http://www.example.com");

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void urlWithSubdomain_ShouldPassValidation() {
        // Arrange
        request.setOriginalUrl("https://subdomain.example.com/path");

        // Act
        Set<ConstraintViolation<CreateShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}

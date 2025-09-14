package com.hitanshudhawan.sankshipt.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeleteShortUrlRequestTest {

    private Validator validator;
    private DeleteShortUrlRequest request;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        request = new DeleteShortUrlRequest();
    }

    @Test
    void validRequest_ShouldPassValidation() {
        // Arrange
        request.setShortCode("abc123");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullShortCode_ShouldFailValidation() {
        // Arrange
        request.setShortCode(null);

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // Both @NotNull and @NotBlank trigger for null values
    }

    @Test
    void emptyShortCode_ShouldFailValidation() {
        // Arrange
        request.setShortCode("");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DeleteShortUrlRequest> violation = violations.iterator().next();
        assertEquals("Short code cannot be blank", violation.getMessage());
        assertEquals("shortCode", violation.getPropertyPath().toString());
    }

    @Test
    void blankShortCode_ShouldFailValidation() {
        // Arrange
        request.setShortCode("   ");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<DeleteShortUrlRequest> violation = violations.iterator().next();
        assertEquals("Short code cannot be blank", violation.getMessage());
        assertEquals("shortCode", violation.getPropertyPath().toString());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String testShortCode = "xyz789";

        // Act
        request.setShortCode(testShortCode);

        // Assert
        assertEquals(testShortCode, request.getShortCode());
    }

    @Test
    void longShortCode_ShouldPassValidation() {
        // Arrange
        String longShortCode = "verylongshortcodewithmanychars123456789";
        request.setShortCode(longShortCode);

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
        assertEquals(longShortCode, request.getShortCode());
    }

    @Test
    void shortCodeWithSpecialChars_ShouldPassValidation() {
        // Arrange
        request.setShortCode("a-b_c1");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void singleCharacterShortCode_ShouldPassValidation() {
        // Arrange
        request.setShortCode("a");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void numericShortCode_ShouldPassValidation() {
        // Arrange
        request.setShortCode("123456");

        // Act
        Set<ConstraintViolation<DeleteShortUrlRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty());
    }
}

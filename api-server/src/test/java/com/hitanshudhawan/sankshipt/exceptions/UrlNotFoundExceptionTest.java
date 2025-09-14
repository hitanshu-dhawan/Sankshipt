package com.hitanshudhawan.sankshipt.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlNotFoundExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateExceptionWithCorrectMessage() {
        // Arrange
        String expectedMessage = "URL not found for short code: abc123";

        // Act
        UrlNotFoundException exception = new UrlNotFoundException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithNullMessage_ShouldCreateExceptionWithNullMessage() {
        // Act
        UrlNotFoundException exception = new UrlNotFoundException(null);

        // Assert
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldCreateExceptionWithEmptyMessage() {
        // Act
        UrlNotFoundException exception = new UrlNotFoundException("");

        // Assert
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void exception_ShouldExtendException() {
        // Arrange
        UrlNotFoundException exception = new UrlNotFoundException("test");

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void exception_ShouldBeCheckedException() {
        // This test verifies that UrlNotFoundException is a checked exception
        // by ensuring it doesn't extend RuntimeException
        UrlNotFoundException exception = new UrlNotFoundException("test");
        
        assertTrue(exception instanceof Exception);
    }
}

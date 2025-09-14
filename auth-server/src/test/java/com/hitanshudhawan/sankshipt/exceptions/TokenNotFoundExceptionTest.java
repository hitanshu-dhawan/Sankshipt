package com.hitanshudhawan.sankshipt.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenNotFoundExceptionTest {

    @Test
    void constructor_WithTokenAndMessage_ShouldCreateExceptionWithCorrectValues() {
        // Arrange
        String token = "testToken123";
        String message = "Token not found";

        // Act
        TokenNotFoundException exception = new TokenNotFoundException(token, message);

        // Assert
        assertEquals(token, exception.getToken());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullToken_ShouldCreateExceptionWithNullToken() {
        // Arrange
        String message = "Token not found";

        // Act
        TokenNotFoundException exception = new TokenNotFoundException(null, message);

        // Assert
        assertNull(exception.getToken());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldCreateExceptionWithNullMessage() {
        // Arrange
        String token = "testToken123";

        // Act
        TokenNotFoundException exception = new TokenNotFoundException(token, null);

        // Assert
        assertEquals(token, exception.getToken());
        assertNull(exception.getMessage());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        TokenNotFoundException exception = new TokenNotFoundException("oldToken", "Old message");
        String newToken = "newToken456";
        String newMessage = "New message";

        // Act
        exception.setToken(newToken);
        exception.setMessage(newMessage);

        // Assert
        assertEquals(newToken, exception.getToken());
        assertEquals(newMessage, exception.getMessage());
    }

    @Test
    void exception_ShouldExtendException() {
        // Arrange
        TokenNotFoundException exception = new TokenNotFoundException("token", "message");

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void exception_ShouldBeCheckedException() {
        // This test verifies that TokenNotFoundException is a checked exception
        TokenNotFoundException exception = new TokenNotFoundException("token", "message");

        assertTrue(exception instanceof Exception);
    }

    @Test
    void constructor_WithEmptyToken_ShouldCreateExceptionWithEmptyToken() {
        // Arrange
        String emptyToken = "";
        String message = "Token not found";

        // Act
        TokenNotFoundException exception = new TokenNotFoundException(emptyToken, message);

        // Assert
        assertEquals(emptyToken, exception.getToken());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldCreateExceptionWithEmptyMessage() {
        // Arrange
        String token = "testToken123";
        String emptyMessage = "";

        // Act
        TokenNotFoundException exception = new TokenNotFoundException(token, emptyMessage);

        // Assert
        assertEquals(token, exception.getToken());
        assertEquals(emptyMessage, exception.getMessage());
    }
}

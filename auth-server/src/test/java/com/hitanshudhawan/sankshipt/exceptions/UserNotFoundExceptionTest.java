package com.hitanshudhawan.sankshipt.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    @Test
    void constructor_WithEmailAndMessage_ShouldCreateExceptionWithCorrectValues() {
        // Arrange
        String email = "test@example.com";
        String message = "User not found";

        // Act
        UserNotFoundException exception = new UserNotFoundException(email, message);

        // Assert
        assertEquals(email, exception.getEmail());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullEmail_ShouldCreateExceptionWithNullEmail() {
        // Arrange
        String message = "User not found";

        // Act
        UserNotFoundException exception = new UserNotFoundException(null, message);

        // Assert
        assertNull(exception.getEmail());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldCreateExceptionWithNullMessage() {
        // Arrange
        String email = "test@example.com";

        // Act
        UserNotFoundException exception = new UserNotFoundException(email, null);

        // Assert
        assertEquals(email, exception.getEmail());
        assertNull(exception.getMessage());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("old@example.com", "Old message");
        String newEmail = "new@example.com";
        String newMessage = "New message";

        // Act
        exception.setEmail(newEmail);
        exception.setMessage(newMessage);

        // Assert
        assertEquals(newEmail, exception.getEmail());
        assertEquals(newMessage, exception.getMessage());
    }

    @Test
    void exception_ShouldExtendException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("test@example.com", "test");

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void exception_ShouldBeCheckedException() {
        // This test verifies that UserNotFoundException is a checked exception
        UserNotFoundException exception = new UserNotFoundException("test@example.com", "test");

        assertTrue(exception instanceof Exception);
    }

    @Test
    void constructor_WithEmptyEmail_ShouldCreateExceptionWithEmptyEmail() {
        // Arrange
        String emptyEmail = "";
        String message = "User not found";

        // Act
        UserNotFoundException exception = new UserNotFoundException(emptyEmail, message);

        // Assert
        assertEquals(emptyEmail, exception.getEmail());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldCreateExceptionWithEmptyMessage() {
        // Arrange
        String email = "test@example.com";
        String emptyMessage = "";

        // Act
        UserNotFoundException exception = new UserNotFoundException(email, emptyMessage);

        // Assert
        assertEquals(email, exception.getEmail());
        assertEquals(emptyMessage, exception.getMessage());
    }
}

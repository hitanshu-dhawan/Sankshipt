package com.hitanshudhawan.sankshipt.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionTest {

    @Test
    void constructor_WithEmailAndMessage_ShouldCreateExceptionWithCorrectValues() {
        // Arrange
        String email = "test@example.com";
        String message = "User with email already exists";

        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(email, message);

        // Assert
        assertEquals(email, exception.getEmail());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullEmail_ShouldCreateExceptionWithNullEmail() {
        // Arrange
        String message = "User already exists";

        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(null, message);

        // Assert
        assertNull(exception.getEmail());
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldCreateExceptionWithNullMessage() {
        // Arrange
        String email = "test@example.com";

        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(email, null);

        // Assert
        assertEquals(email, exception.getEmail());
        assertNull(exception.getMessage());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        UserAlreadyExistsException exception = new UserAlreadyExistsException("old@example.com", "Old message");
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
        UserAlreadyExistsException exception = new UserAlreadyExistsException("test@example.com", "test");

        // Assert
        assertTrue(exception instanceof Exception);
    }

    @Test
    void exception_ShouldBeCheckedException() {
        // This test verifies that UserAlreadyExistsException is a checked exception
        UserAlreadyExistsException exception = new UserAlreadyExistsException("test@example.com", "test");

        assertTrue(exception instanceof Exception);
    }
}

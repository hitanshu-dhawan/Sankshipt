package com.hitanshudhawan.sankshipt.exceptionhandlers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlersTest {

    @InjectMocks
    private ExceptionHandlers exceptionHandlers;

    private UrlNotFoundException urlNotFoundException;

    @BeforeEach
    void setUp() {
        urlNotFoundException = new UrlNotFoundException("Test URL not found message");
    }

    @Test
    void handleProductNotFoundException_ShouldReturnNotFoundWithMessage() {
        // Act
        ResponseEntity<String> response = exceptionHandlers.handleProductNotFoundException(urlNotFoundException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Test URL not found message", response.getBody());
    }

    @Test
    void handleProductNotFoundException_WithNullMessage_ShouldReturnNotFoundWithNullBody() {
        // Arrange
        UrlNotFoundException exceptionWithNullMessage = new UrlNotFoundException(null);

        // Act
        ResponseEntity<String> response = exceptionHandlers.handleProductNotFoundException(exceptionWithNullMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void handleProductNotFoundException_WithEmptyMessage_ShouldReturnNotFoundWithEmptyBody() {
        // Arrange
        UrlNotFoundException exceptionWithEmptyMessage = new UrlNotFoundException("");

        // Act
        ResponseEntity<String> response = exceptionHandlers.handleProductNotFoundException(exceptionWithEmptyMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void handleProductNotFoundException_WithLongMessage_ShouldReturnNotFoundWithFullMessage() {
        // Arrange
        String longMessage = "This is a very long error message that should be returned in full without any truncation or modification";
        UrlNotFoundException exceptionWithLongMessage = new UrlNotFoundException(longMessage);

        // Act
        ResponseEntity<String> response = exceptionHandlers.handleProductNotFoundException(exceptionWithLongMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(longMessage, response.getBody());
    }
}

package com.hitanshudhawan.sankshipt.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ClickResponseTest {

    private ClickResponse clickResponse;

    @BeforeEach
    void setUp() {
        clickResponse = new ClickResponse();
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        String shortCode = "abc123";
        String originalUrl = "https://www.example.com";
        Date clickedAt = new Date();
        String userAgent = "Mozilla/5.0";

        // Act
        clickResponse.setId(id);
        clickResponse.setShortCode(shortCode);
        clickResponse.setOriginalUrl(originalUrl);
        clickResponse.setClickedAt(clickedAt);
        clickResponse.setUserAgent(userAgent);

        // Assert
        assertEquals(id, clickResponse.getId());
        assertEquals(shortCode, clickResponse.getShortCode());
        assertEquals(originalUrl, clickResponse.getOriginalUrl());
        assertEquals(clickedAt, clickResponse.getClickedAt());
        assertEquals(userAgent, clickResponse.getUserAgent());
    }

    @Test
    void constructor_ShouldCreateEmptyObject() {
        // Assert
        assertNull(clickResponse.getId());
        assertNull(clickResponse.getShortCode());
        assertNull(clickResponse.getOriginalUrl());
        assertNull(clickResponse.getClickedAt());
        assertNull(clickResponse.getUserAgent());
    }

    @Test
    void setId_WithNull_ShouldAcceptNull() {
        // Act
        clickResponse.setId(null);

        // Assert
        assertNull(clickResponse.getId());
    }

    @Test
    void setShortCode_WithNull_ShouldAcceptNull() {
        // Act
        clickResponse.setShortCode(null);

        // Assert
        assertNull(clickResponse.getShortCode());
    }

    @Test
    void setOriginalUrl_WithNull_ShouldAcceptNull() {
        // Act
        clickResponse.setOriginalUrl(null);

        // Assert
        assertNull(clickResponse.getOriginalUrl());
    }

    @Test
    void setClickedAt_WithNull_ShouldAcceptNull() {
        // Act
        clickResponse.setClickedAt(null);

        // Assert
        assertNull(clickResponse.getClickedAt());
    }

    @Test
    void setUserAgent_WithNull_ShouldAcceptNull() {
        // Act
        clickResponse.setUserAgent(null);

        // Assert
        assertNull(clickResponse.getUserAgent());
    }

    @Test
    void setUserAgent_WithLongUserAgent_ShouldAcceptLongUserAgent() {
        // Arrange
        String longUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

        // Act
        clickResponse.setUserAgent(longUserAgent);

        // Assert
        assertEquals(longUserAgent, clickResponse.getUserAgent());
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Arrange
        Date now = new Date();
        
        ClickResponse response1 = new ClickResponse();
        response1.setId(1L);
        response1.setShortCode("abc123");
        response1.setOriginalUrl("https://www.example.com");
        response1.setClickedAt(now);
        response1.setUserAgent("Mozilla/5.0");

        ClickResponse response2 = new ClickResponse();
        response2.setId(1L);
        response2.setShortCode("abc123");
        response2.setOriginalUrl("https://www.example.com");
        response2.setClickedAt(now);
        response2.setUserAgent("Mozilla/5.0");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        ClickResponse response1 = new ClickResponse();
        response1.setId(1L);
        response1.setShortCode("abc123");

        ClickResponse response2 = new ClickResponse();
        response2.setId(2L);
        response2.setShortCode("xyz789");

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void toString_ShouldIncludeFieldValues() {
        // Arrange
        clickResponse.setId(1L);
        clickResponse.setShortCode("abc123");
        clickResponse.setOriginalUrl("https://www.example.com");
        clickResponse.setUserAgent("Mozilla/5.0");

        // Act
        String toString = clickResponse.toString();

        // Assert
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("abc123"));
        assertTrue(toString.contains("https://www.example.com"));
        assertTrue(toString.contains("Mozilla/5.0"));
    }

    @Test
    void setId_WithZero_ShouldAcceptZero() {
        // Act
        clickResponse.setId(0L);

        // Assert
        assertEquals(0L, clickResponse.getId());
    }

    @Test
    void setId_WithNegativeValue_ShouldAcceptNegativeValue() {
        // Act
        clickResponse.setId(-1L);

        // Assert
        assertEquals(-1L, clickResponse.getId());
    }
}

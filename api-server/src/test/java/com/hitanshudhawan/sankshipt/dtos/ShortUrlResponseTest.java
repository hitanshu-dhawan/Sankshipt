package com.hitanshudhawan.sankshipt.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortUrlResponseTest {

    private ShortUrlResponse response;

    @BeforeEach
    void setUp() {
        response = new ShortUrlResponse();
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String originalUrl = "https://www.example.com";
        String shortCode = "abc123";

        // Act
        response.setOriginalUrl(originalUrl);
        response.setShortCode(shortCode);

        // Assert
        assertEquals(originalUrl, response.getOriginalUrl());
        assertEquals(shortCode, response.getShortCode());
    }

    @Test
    void constructor_ShouldCreateEmptyObject() {
        // Assert
        assertNull(response.getOriginalUrl());
        assertNull(response.getShortCode());
    }

    @Test
    void setOriginalUrl_WithNull_ShouldAcceptNull() {
        // Act
        response.setOriginalUrl(null);

        // Assert
        assertNull(response.getOriginalUrl());
    }

    @Test
    void setShortCode_WithNull_ShouldAcceptNull() {
        // Act
        response.setShortCode(null);

        // Assert
        assertNull(response.getShortCode());
    }

    @Test
    void setOriginalUrl_WithEmptyString_ShouldAcceptEmptyString() {
        // Act
        response.setOriginalUrl("");

        // Assert
        assertEquals("", response.getOriginalUrl());
    }

    @Test
    void setShortCode_WithEmptyString_ShouldAcceptEmptyString() {
        // Act
        response.setShortCode("");

        // Assert
        assertEquals("", response.getShortCode());
    }

    @Test
    void setOriginalUrl_WithLongUrl_ShouldAcceptLongUrl() {
        // Arrange
        String longUrl = "https://www.example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2";

        // Act
        response.setOriginalUrl(longUrl);

        // Assert
        assertEquals(longUrl, response.getOriginalUrl());
    }

    @Test
    void setShortCode_WithSpecialCharacters_ShouldAcceptSpecialCharacters() {
        // Arrange
        String shortCodeWithSpecialChars = "a-b_c1";

        // Act
        response.setShortCode(shortCodeWithSpecialChars);

        // Assert
        assertEquals(shortCodeWithSpecialChars, response.getShortCode());
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Arrange
        ShortUrlResponse response1 = new ShortUrlResponse();
        response1.setOriginalUrl("https://www.example.com");
        response1.setShortCode("abc123");

        ShortUrlResponse response2 = new ShortUrlResponse();
        response2.setOriginalUrl("https://www.example.com");
        response2.setShortCode("abc123");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        ShortUrlResponse response1 = new ShortUrlResponse();
        response1.setOriginalUrl("https://www.example.com");
        response1.setShortCode("abc123");

        ShortUrlResponse response2 = new ShortUrlResponse();
        response2.setOriginalUrl("https://www.different.com");
        response2.setShortCode("xyz789");

        // Assert
        assertNotEquals(response1, response2);
    }

    @Test
    void toString_ShouldIncludeFieldValues() {
        // Arrange
        response.setOriginalUrl("https://www.example.com");
        response.setShortCode("abc123");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("https://www.example.com"));
        assertTrue(toString.contains("abc123"));
    }
}

package com.hitanshudhawan.sankshipt.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ShortCodeGenerator utility
 */
class ShortCodeGeneratorTest {

    @Test
    void testGenerateShortCode() {
        String originalUrl = "https://www.amazon.com/product/12345";
        Long id = 500L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, originalUrl);
        
        assertNotNull(shortCode);
        assertTrue(shortCode.length() > 2); // Should have Base62 part + checksum
        
        // Test that same inputs produce same output
        String shortCode2 = ShortCodeGenerator.generateShortCode(id, originalUrl);
        assertEquals(shortCode, shortCode2);
    }

    @Test
    void testExtractIdFromShortCode() {
        String originalUrl = "https://www.google.com";
        Long originalId = 125L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(originalId, originalUrl);
        Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode);
        
        assertEquals(originalId, extractedId);
    }

    @Test
    void testValidateShortCode() {
        String originalUrl = "https://www.example.com/path";
        Long id = 999L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, originalUrl);
        
        // Valid case
        assertTrue(ShortCodeGenerator.validateShortCode(shortCode, originalUrl));
        
        // Invalid case - different URL
        assertFalse(ShortCodeGenerator.validateShortCode(shortCode, "https://different-url.com"));
        
        // Invalid case - null URL
        assertFalse(ShortCodeGenerator.validateShortCode(shortCode, null));
        
        // Invalid case - null short code
        assertFalse(ShortCodeGenerator.validateShortCode(null, originalUrl));
    }

    @Test
    void testGenerateShortCode_WithZeroId() {
        String originalUrl = "https://www.example.com";
        Long id = 0L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, originalUrl);
        
        assertNotNull(shortCode);
        assertTrue(shortCode.length() >= 6); // At least checksum length
        
        Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode);
        assertEquals(id, extractedId);
    }

    @Test
    void testGenerateShortCode_WithLargeId() {
        String originalUrl = "https://www.example.com";
        Long id = 999999999L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, originalUrl);
        
        assertNotNull(shortCode);
        assertTrue(shortCode.length() > 6); // Base62 part + checksum
        
        Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode);
        assertEquals(id, extractedId);
    }

    @Test
    void testGenerateShortCode_WithNullId_ShouldThrowException() {
        String originalUrl = "https://www.example.com";
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.generateShortCode(null, originalUrl));
    }

    @Test
    void testGenerateShortCode_WithNegativeId_ShouldThrowException() {
        String originalUrl = "https://www.example.com";
        Long negativeId = -1L;
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.generateShortCode(negativeId, originalUrl));
    }

    @Test
    void testGenerateShortCode_WithNullUrl_ShouldThrowException() {
        Long id = 123L;
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.generateShortCode(id, null));
    }

    @Test
    void testGenerateShortCode_WithEmptyUrl_ShouldThrowException() {
        Long id = 123L;
        String emptyUrl = "";
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.generateShortCode(id, emptyUrl));
    }

    @Test
    void testGenerateShortCode_WithBlankUrl_ShouldThrowException() {
        Long id = 123L;
        String blankUrl = "   ";
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.generateShortCode(id, blankUrl));
    }

    @Test
    void testExtractIdFromShortCode_WithNullShortCode_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.extractIdFromShortCode(null));
    }

    @Test
    void testExtractIdFromShortCode_WithTooShortCode_ShouldThrowException() {
        String tooShortCode = "abc"; // Less than checksum length
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.extractIdFromShortCode(tooShortCode));
    }

    @Test
    void testExtractIdFromShortCode_WithInvalidCharacters_ShouldThrowException() {
        String invalidShortCode = "abc123@#$def";
        
        assertThrows(IllegalArgumentException.class, 
                () -> ShortCodeGenerator.extractIdFromShortCode(invalidShortCode));
    }

    @Test
    void testValidateShortCode_WithTooShortCode_ShouldReturnFalse() {
        String tooShortCode = "abc";
        String originalUrl = "https://www.example.com";
        
        assertFalse(ShortCodeGenerator.validateShortCode(tooShortCode, originalUrl));
    }

    @Test
    void testValidateShortCode_WithEmptyUrl_ShouldReturnFalse() {
        String shortCode = "abc123456";
        String emptyUrl = "";
        
        assertFalse(ShortCodeGenerator.validateShortCode(shortCode, emptyUrl));
    }

    @Test
    void testValidateShortCode_BothNullInputs_ShouldReturnFalse() {
        assertFalse(ShortCodeGenerator.validateShortCode(null, null));
    }

    @Test
    void testDifferentUrlsSameId_ShouldGenerateDifferentShortCodes() {
        Long id = 123L;
        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        
        String shortCode1 = ShortCodeGenerator.generateShortCode(id, url1);
        String shortCode2 = ShortCodeGenerator.generateShortCode(id, url2);
        
        assertNotEquals(shortCode1, shortCode2);
    }

    @Test
    void testSameUrlDifferentIds_ShouldGenerateDifferentShortCodes() {
        String originalUrl = "https://www.example.com";
        Long id1 = 123L;
        Long id2 = 456L;
        
        String shortCode1 = ShortCodeGenerator.generateShortCode(id1, originalUrl);
        String shortCode2 = ShortCodeGenerator.generateShortCode(id2, originalUrl);
        
        assertNotEquals(shortCode1, shortCode2);
    }

    @Test
    void testConsistencyAcrossMultipleCalls() {
        String originalUrl = "https://www.consistency-test.com";
        Long id = 789L;
        
        for (int i = 0; i < 100; i++) {
            String shortCode1 = ShortCodeGenerator.generateShortCode(id, originalUrl);
            String shortCode2 = ShortCodeGenerator.generateShortCode(id, originalUrl);
            assertEquals(shortCode1, shortCode2, "Short codes should be consistent across calls");
            
            Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode1);
            assertEquals(id, extractedId, "Extracted ID should match original ID");
            
            assertTrue(ShortCodeGenerator.validateShortCode(shortCode1, originalUrl), 
                    "Short code should validate correctly");
        }
    }

    @Test
    void testWithSpecialCharactersInUrl() {
        String specialUrl = "https://www.example.com/path?param=value&another=123#fragment";
        Long id = 555L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, specialUrl);
        
        assertNotNull(shortCode);
        assertTrue(ShortCodeGenerator.validateShortCode(shortCode, specialUrl));
        
        Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode);
        assertEquals(id, extractedId);
    }

    @Test
    void testWithVeryLongUrl() {
        StringBuilder longUrlBuilder = new StringBuilder("https://www.example.com/");
        for (int i = 0; i < 1000; i++) {
            longUrlBuilder.append("very-long-path-segment-").append(i).append("/");
        }
        String longUrl = longUrlBuilder.toString();
        Long id = 777L;
        
        String shortCode = ShortCodeGenerator.generateShortCode(id, longUrl);
        
        assertNotNull(shortCode);
        assertTrue(ShortCodeGenerator.validateShortCode(shortCode, longUrl));
        
        Long extractedId = ShortCodeGenerator.extractIdFromShortCode(shortCode);
        assertEquals(id, extractedId);
    }
}

package com.hitanshudhawan.sankshipt.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating and decoding short codes using Base62 encoding
 * combined with MD5 hash checksum for URL shortening.
 * 
 * Approach: Base62(ID) + MD5_Hash_Checksum(originalUrl)
 * Example: ID=500 -> "8g" + hash("example.com") -> "8s5ababd"
 */
public class ShortCodeGenerator {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE62_BASE = BASE62_CHARS.length();
    private static final int CHECKSUM_LENGTH = 6; // Number of characters from MD5 hash to use as checksum

    /**
     * Generates a short code using the hybrid approach: Base62(ID) + MD5 checksum
     * 
     * @param id The database ID of the URL record
     * @param originalUrl The original long URL
     * @return The generated short code
     */
    public static String generateShortCode(Long id, String originalUrl) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID must be a non-negative number");
        }
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Original URL cannot be null or empty");
        }

        String base62Id = encodeToBase62(id);
        String checksum = generateChecksum(originalUrl);
        
        return base62Id + checksum;
    }

    /**
     * Extracts the ID from a short code by decoding the Base62 part
     * 
     * @param shortCode The short code to decode
     * @return The extracted ID
     */
    public static Long extractIdFromShortCode(String shortCode) {
        if (shortCode == null || shortCode.length() <= CHECKSUM_LENGTH) {
            throw new IllegalArgumentException("Invalid short code format");
        }

        // Extract the Base62 part (everything except the last CHECKSUM_LENGTH characters)
        String base62Part = shortCode.substring(0, shortCode.length() - CHECKSUM_LENGTH);
        
        return decodeFromBase62(base62Part);
    }

    /**
     * Validates if a short code matches the original URL by checking the checksum
     * 
     * @param shortCode The short code to validate
     * @param originalUrl The original URL to validate against
     * @return true if the checksum matches, false otherwise
     */
    public static boolean validateShortCode(String shortCode, String originalUrl) {
        if (shortCode == null || shortCode.length() <= CHECKSUM_LENGTH || originalUrl == null) {
            return false;
        }

        // Extract the checksum part (last CHECKSUM_LENGTH characters)
        String providedChecksum = shortCode.substring(shortCode.length() - CHECKSUM_LENGTH);
        String expectedChecksum = generateChecksum(originalUrl);
        
        return providedChecksum.equals(expectedChecksum);
    }

    /**
     * Encodes a number to Base62 string
     * 
     * @param number The number to encode
     * @return The Base62 encoded string
     */
    private static String encodeToBase62(Long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder result = new StringBuilder();
        long num = number;
        
        while (num > 0) {
            result.insert(0, BASE62_CHARS.charAt((int) (num % BASE62_BASE)));
            num /= BASE62_BASE;
        }
        
        return result.toString();
    }

    /**
     * Decodes a Base62 string back to a number
     * 
     * @param base62String The Base62 string to decode
     * @return The decoded number
     */
    private static Long decodeFromBase62(String base62String) {
        if (base62String == null || base62String.isEmpty()) {
            throw new IllegalArgumentException("Base62 string cannot be null or empty");
        }

        long result = 0;
        long power = 1;
        
        // Process from right to left
        for (int i = base62String.length() - 1; i >= 0; i--) {
            char ch = base62String.charAt(i);
            int charIndex = BASE62_CHARS.indexOf(ch);
            
            if (charIndex == -1) {
                throw new IllegalArgumentException("Invalid character in Base62 string: " + ch);
            }
            
            result += charIndex * power;
            power *= BASE62_BASE;
        }
        
        return result;
    }

    /**
     * Generates a checksum from the original URL using MD5 hash
     * 
     * @param originalUrl The original URL to generate checksum for
     * @return The first CHECKSUM_LENGTH characters of the MD5 hash
     */
    private static String generateChecksum(String originalUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(originalUrl.getBytes());
            
            // Convert hash bytes to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            // Return first CHECKSUM_LENGTH characters
            return hexString.substring(0, Math.min(CHECKSUM_LENGTH, hexString.length()));
            
        } catch (NoSuchAlgorithmException e) {
            // This should never happen as MD5 is always available
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

}

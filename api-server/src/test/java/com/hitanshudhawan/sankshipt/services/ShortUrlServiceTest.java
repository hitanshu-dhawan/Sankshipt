package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.ShortUrlRepository;
import com.hitanshudhawan.sankshipt.utils.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private ShortUrlServiceImpl shortUrlService;

    private User testUser;
    private URL testUrl;
    private final String originalUrl = "https://www.example.com";
    private final String shortCode = "abc123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testUrl = new URL();
        testUrl.setId(1L);
        testUrl.setOriginalUrl(originalUrl);
        testUrl.setShortCode(shortCode);
        testUrl.setUser(testUser);
    }

    @Test
    void createShortUrl_ShouldCreateAndReturnUrl() {
        // Arrange
        URL savedUrlWithId = new URL();
        savedUrlWithId.setId(1L);
        savedUrlWithId.setOriginalUrl(originalUrl);
        savedUrlWithId.setUser(testUser);

        URL finalSavedUrl = new URL();
        finalSavedUrl.setId(1L);
        finalSavedUrl.setOriginalUrl(originalUrl);
        finalSavedUrl.setShortCode(shortCode);
        finalSavedUrl.setUser(testUser);

        when(shortUrlRepository.save(any(URL.class)))
                .thenReturn(savedUrlWithId)
                .thenReturn(finalSavedUrl);

        try (MockedStatic<ShortCodeGenerator> mockedStatic = mockStatic(ShortCodeGenerator.class)) {
            mockedStatic.when(() -> ShortCodeGenerator.generateShortCode(1L, originalUrl))
                    .thenReturn(shortCode);

            // Act
            URL result = shortUrlService.createShortUrl(originalUrl, testUser);

            // Assert
            assertNotNull(result);
            assertEquals(originalUrl, result.getOriginalUrl());
            assertEquals(shortCode, result.getShortCode());
            assertEquals(testUser, result.getUser());
            verify(shortUrlRepository, times(2)).save(any(URL.class));
            mockedStatic.verify(() -> ShortCodeGenerator.generateShortCode(1L, originalUrl));
        }
    }

    @Test
    void resolveShortCode_ValidShortCode_ShouldReturnUrl() throws UrlNotFoundException {
        // Arrange
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(testUrl));

        try (MockedStatic<ShortCodeGenerator> mockedStatic = mockStatic(ShortCodeGenerator.class)) {
            mockedStatic.when(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl))
                    .thenReturn(true);

            // Act
            URL result = shortUrlService.resolveShortCode(shortCode);

            // Assert
            assertNotNull(result);
            assertEquals(testUrl, result);
            verify(shortUrlRepository).findByShortCode(shortCode);
            mockedStatic.verify(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl));
        }
    }

    @Test
    void resolveShortCode_NonExistentShortCode_ShouldThrowException() {
        // Arrange
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // Act & Assert
        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, 
                () -> shortUrlService.resolveShortCode(shortCode));
        assertTrue(exception.getMessage().contains("No URL mapping found for short code"));
        verify(shortUrlRepository).findByShortCode(shortCode);
    }

    @Test
    void resolveShortCode_InvalidShortCode_ShouldThrowException() {
        // Arrange
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(testUrl));

        try (MockedStatic<ShortCodeGenerator> mockedStatic = mockStatic(ShortCodeGenerator.class)) {
            mockedStatic.when(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl))
                    .thenReturn(false);

            // Act & Assert
            UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, 
                    () -> shortUrlService.resolveShortCode(shortCode));
            assertTrue(exception.getMessage().contains("failed validation"));
            verify(shortUrlRepository).findByShortCode(shortCode);
            mockedStatic.verify(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl));
        }
    }

    @Test
    void deleteShortUrl_ValidUserAndUrl_ShouldDeleteUrl() throws UrlNotFoundException {
        // Arrange
        when(shortUrlRepository.findByShortCodeAndUser(shortCode, testUser))
                .thenReturn(Optional.of(testUrl));

        try (MockedStatic<ShortCodeGenerator> mockedStatic = mockStatic(ShortCodeGenerator.class)) {
            mockedStatic.when(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl))
                    .thenReturn(true);

            // Act
            shortUrlService.deleteShortUrl(shortCode, testUser);

            // Assert
            verify(shortUrlRepository).findByShortCodeAndUser(shortCode, testUser);
            verify(shortUrlRepository).delete(testUrl);
            mockedStatic.verify(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl));
        }
    }

    @Test
    void deleteShortUrl_NonExistentUrl_ShouldThrowException() {
        // Arrange
        when(shortUrlRepository.findByShortCodeAndUser(shortCode, testUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, 
                () -> shortUrlService.deleteShortUrl(shortCode, testUser));
        assertTrue(exception.getMessage().contains("No URL mapping found"));
        verify(shortUrlRepository).findByShortCodeAndUser(shortCode, testUser);
        verify(shortUrlRepository, never()).delete(any());
    }

    @Test
    void deleteShortUrl_InvalidShortCode_ShouldThrowException() {
        // Arrange
        when(shortUrlRepository.findByShortCodeAndUser(shortCode, testUser))
                .thenReturn(Optional.of(testUrl));

        try (MockedStatic<ShortCodeGenerator> mockedStatic = mockStatic(ShortCodeGenerator.class)) {
            mockedStatic.when(() -> ShortCodeGenerator.validateShortCode(shortCode, originalUrl))
                    .thenReturn(false);

            // Act & Assert
            UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, 
                    () -> shortUrlService.deleteShortUrl(shortCode, testUser));
            assertTrue(exception.getMessage().contains("failed validation"));
            verify(shortUrlRepository).findByShortCodeAndUser(shortCode, testUser);
            verify(shortUrlRepository, never()).delete(any());
        }
    }

    @Test
    void isUrlOwner_UserOwnsUrl_ShouldReturnTrue() throws UrlNotFoundException {
        // Arrange
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(testUrl));

        // Act
        boolean result = shortUrlService.isUrlOwner(shortCode, testUser);

        // Assert
        assertTrue(result);
        verify(shortUrlRepository).findByShortCode(shortCode);
    }

    @Test
    void isUrlOwner_UserDoesNotOwnUrl_ShouldReturnFalse() throws UrlNotFoundException {
        // Arrange
        User differentUser = new User();
        differentUser.setEmail("different@example.com");
        
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.of(testUrl));

        // Act
        boolean result = shortUrlService.isUrlOwner(shortCode, differentUser);

        // Assert
        assertFalse(result);
        verify(shortUrlRepository).findByShortCode(shortCode);
    }

    @Test
    void isUrlOwner_NonExistentUrl_ShouldThrowException() {
        // Arrange
        when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // Act & Assert
        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, 
                () -> shortUrlService.isUrlOwner(shortCode, testUser));
        assertTrue(exception.getMessage().contains("No URL mapping found"));
        verify(shortUrlRepository).findByShortCode(shortCode);
    }
}

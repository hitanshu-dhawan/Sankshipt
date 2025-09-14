package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = UrlController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration.class
})
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortUrlService shortUrlService;

    @MockBean
    private ClickAnalyticsService clickAnalyticsService;

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
    void redirectToUrl_ValidShortCode_ShouldRedirectToOriginalUrl() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);

        // Act & Assert
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl(originalUrl));

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(clickAnalyticsService).recordClick(eq(testUrl), any(HttpServletRequest.class));
    }

    @Test
    void redirectToUrl_NonExistentShortCode_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode))
                .thenThrow(new UrlNotFoundException("URL not found"));

        // Act & Assert
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isNotFound());

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(clickAnalyticsService, never()).recordClick(any(), any());
    }

    @Test
    void redirectToUrl_AnalyticsFailure_ShouldStillRedirect() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        doThrow(new RuntimeException("Analytics service down"))
                .when(clickAnalyticsService).recordClick(eq(testUrl), any(HttpServletRequest.class));

        // Act & Assert
        mockMvc.perform(get("/" + shortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl(originalUrl));

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(clickAnalyticsService).recordClick(eq(testUrl), any(HttpServletRequest.class));
    }

    @Test
    void redirectToUrl_EmptyShortCode_ShouldHandleGracefully() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isNotFound()); // Spring Boot default behavior for unmapped paths

        verify(shortUrlService, never()).resolveShortCode(any());
        verify(clickAnalyticsService, never()).recordClick(any(), any());
    }

    @Test
    void redirectToUrl_LongShortCode_ShouldProcessNormally() throws Exception {
        // Arrange
        String longShortCode = "verylongshortcodethatisunusual123456789";
        URL longCodeUrl = new URL();
        longCodeUrl.setId(2L);
        longCodeUrl.setOriginalUrl("https://www.longurl.com");
        longCodeUrl.setShortCode(longShortCode);
        longCodeUrl.setUser(testUser);

        when(shortUrlService.resolveShortCode(longShortCode)).thenReturn(longCodeUrl);

        // Act & Assert
        mockMvc.perform(get("/" + longShortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("https://www.longurl.com"));

        verify(shortUrlService).resolveShortCode(longShortCode);
        verify(clickAnalyticsService).recordClick(eq(longCodeUrl), any(HttpServletRequest.class));
    }

    @Test
    void redirectToUrl_SpecialCharactersInShortCode_ShouldProcessNormally() throws Exception {
        // Arrange
        String specialShortCode = "a-b_c1";
        URL specialCodeUrl = new URL();
        specialCodeUrl.setId(3L);
        specialCodeUrl.setOriginalUrl("https://www.special-url.com");
        specialCodeUrl.setShortCode(specialShortCode);
        specialCodeUrl.setUser(testUser);

        when(shortUrlService.resolveShortCode(specialShortCode)).thenReturn(specialCodeUrl);

        // Act & Assert
        mockMvc.perform(get("/" + specialShortCode))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrl("https://www.special-url.com"));

        verify(shortUrlService).resolveShortCode(specialShortCode);
        verify(clickAnalyticsService).recordClick(eq(specialCodeUrl), any(HttpServletRequest.class));
    }
}

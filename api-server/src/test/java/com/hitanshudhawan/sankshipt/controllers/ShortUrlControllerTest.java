package com.hitanshudhawan.sankshipt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitanshudhawan.sankshipt.configs.SecurityConfig;
import com.hitanshudhawan.sankshipt.dtos.CreateShortUrlRequest;
import com.hitanshudhawan.sankshipt.dtos.DeleteShortUrlRequest;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.AuthenticationService;
import com.hitanshudhawan.sankshipt.services.ShortUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ShortUrlController.class)
@Import(ShortUrlControllerTest.TestSecurityConfig.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortUrlService shortUrlService;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(authorities = "SCOPE_api.write")
    void createShortUrl_ValidRequest_ShouldReturnCreatedUrl() throws Exception {
        // Arrange
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl(originalUrl);

        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.createShortUrl(originalUrl, testUser)).thenReturn(testUrl);

        // Act & Assert
        mockMvc.perform(post("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortCode").value(shortCode));

        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).createShortUrl(originalUrl, testUser);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.write")
    void createShortUrl_InvalidUrl_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl(""); // Invalid empty URL

        // Act & Assert
        mockMvc.perform(post("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authenticationService, never()).getCurrentUser();
        verify(shortUrlService, never()).createShortUrl(any(), any());
    }

    @Test
    void createShortUrl_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl(originalUrl);

        // Act & Assert
        mockMvc.perform(post("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authenticationService, never()).getCurrentUser();
        verify(shortUrlService, never()).createShortUrl(any(), any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.delete")
    void deleteShortUrl_ValidRequestAndOwner_ShouldDeleteUrl() throws Exception {
        // Arrange
        DeleteShortUrlRequest request = new DeleteShortUrlRequest();
        request.setShortCode(shortCode);

        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.shortCode").value(shortCode));

        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).resolveShortCode(shortCode);
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(shortUrlService).deleteShortUrl(shortCode, testUser);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.delete")
    void deleteShortUrl_NotOwner_ShouldReturnForbidden() throws Exception {
        // Arrange
        DeleteShortUrlRequest request = new DeleteShortUrlRequest();
        request.setShortCode(shortCode);

        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).resolveShortCode(shortCode);
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(shortUrlService, never()).deleteShortUrl(any(), any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.delete")
    void deleteShortUrl_NonExistentUrl_ShouldReturnNotFound() throws Exception {
        // Arrange
        DeleteShortUrlRequest request = new DeleteShortUrlRequest();
        request.setShortCode(shortCode);

        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.resolveShortCode(shortCode))
                .thenThrow(new UrlNotFoundException("URL not found"));

        // Act & Assert
        mockMvc.perform(delete("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).resolveShortCode(shortCode);
        verify(shortUrlService, never()).isUrlOwner(any(), any());
        verify(shortUrlService, never()).deleteShortUrl(any(), any());
    }

    @Test
    void deleteShortUrl_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        DeleteShortUrlRequest request = new DeleteShortUrlRequest();
        request.setShortCode(shortCode);

        // Act & Assert
        mockMvc.perform(delete("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authenticationService, never()).getCurrentUser();
        verify(shortUrlService, never()).resolveShortCode(any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.write") // Wrong authority
    void deleteShortUrl_WithoutDeleteAuthority_ShouldReturnForbidden() throws Exception {
        // Arrange
        DeleteShortUrlRequest request = new DeleteShortUrlRequest();
        request.setShortCode(shortCode);

        // Act & Assert
        mockMvc.perform(delete("/api/urls")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authenticationService, never()).getCurrentUser();
        verify(shortUrlService, never()).resolveShortCode(any());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        // This enables method-level security for the test
    }
}

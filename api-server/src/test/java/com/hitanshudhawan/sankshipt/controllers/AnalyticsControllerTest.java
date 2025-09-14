package com.hitanshudhawan.sankshipt.controllers;

import com.hitanshudhawan.sankshipt.configs.SecurityConfig;
import com.hitanshudhawan.sankshipt.exceptions.UrlNotFoundException;
import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.AuthenticationService;
import com.hitanshudhawan.sankshipt.services.ClickAnalyticsService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AnalyticsController.class)
@Import(AnalyticsControllerTest.TestSecurityConfig.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortUrlService shortUrlService;

    @MockBean
    private ClickAnalyticsService clickAnalyticsService;

    @MockBean
    private AuthenticationService authenticationService;

    private User testUser;
    private URL testUrl;
    private Click testClick;
    private final String originalUrl = "https://www.example.com";
    private final String shortCode = "abc123";
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

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

        testClick = new Click();
        testClick.setId(1L);
        testClick.setUrl(testUrl);
        testClick.setUserAgent(userAgent);
        testClick.setClickedAt(new Date());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClickCount_ValidRequestAndOwner_ShouldReturnClickCount() throws Exception {
        // Arrange
        Long expectedCount = 42L;
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(true);
        when(clickAnalyticsService.getClickCountForUrl(testUrl)).thenReturn(expectedCount);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/count"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedCount.toString()));

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(clickAnalyticsService).getClickCountForUrl(testUrl);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClickCount_NotOwner_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/count"))
                .andExpect(status().isForbidden());

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(clickAnalyticsService, never()).getClickCountForUrl(any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClickCount_NonExistentUrl_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode))
                .thenThrow(new UrlNotFoundException("URL not found"));

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/count"))
                .andExpect(status().isNotFound());

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(authenticationService, never()).getCurrentUser();
        verify(clickAnalyticsService, never()).getClickCountForUrl(any());
    }

    @Test
    void getShortUrlClickCount_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/count"))
                .andExpect(status().isUnauthorized());

        verify(shortUrlService, never()).resolveShortCode(any());
        verify(authenticationService, never()).getCurrentUser();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClicks_ValidRequestAndOwner_ShouldReturnClicks() throws Exception {
        // Arrange
        Page<Click> clickPage = new PageImpl<>(Arrays.asList(testClick));
        
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(true);
        when(clickAnalyticsService.getClicksForUrl(eq(testUrl), any(), any(), any()))
                .thenReturn(clickPage);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testClick.getId()))
                .andExpect(jsonPath("$.content[0].shortCode").value(shortCode))
                .andExpect(jsonPath("$.content[0].originalUrl").value(originalUrl))
                .andExpect(jsonPath("$.content[0].userAgent").value(userAgent));

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(clickAnalyticsService).getClicksForUrl(eq(testUrl), eq(null), eq(null), eq(null));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClicks_WithPaginationParameters_ShouldPassParametersCorrectly() throws Exception {
        // Arrange
        Page<Click> clickPage = new PageImpl<>(Arrays.asList(testClick));
        Integer pageNumber = 2;
        Integer pageSize = 10;
        String sortOrder = "ASC";
        
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(true);
        when(clickAnalyticsService.getClicksForUrl(eq(testUrl), eq(pageNumber), eq(pageSize), eq(sortOrder)))
                .thenReturn(clickPage);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks")
                        .param("pageNumber", pageNumber.toString())
                        .param("pageSize", pageSize.toString())
                        .param("sortOrder", sortOrder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testClick.getId()));

        verify(clickAnalyticsService).getClicksForUrl(eq(testUrl), eq(pageNumber), eq(pageSize), eq(sortOrder));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClicks_NotOwner_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks"))
                .andExpect(status().isForbidden());

        verify(shortUrlService).resolveShortCode(shortCode);
        verify(authenticationService).getCurrentUser();
        verify(shortUrlService).isUrlOwner(shortCode, testUser);
        verify(clickAnalyticsService, never()).getClicksForUrl(any(), any(), any(), any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.read")
    void getShortUrlClicks_EmptyResults_ShouldReturnEmptyPage() throws Exception {
        // Arrange
        Page<Click> emptyPage = new PageImpl<>(Arrays.asList());
        
        when(shortUrlService.resolveShortCode(shortCode)).thenReturn(testUrl);
        when(authenticationService.getCurrentUser()).thenReturn(testUser);
        when(shortUrlService.isUrlOwner(shortCode, testUser)).thenReturn(true);
        when(clickAnalyticsService.getClicksForUrl(eq(testUrl), any(), any(), any()))
                .thenReturn(emptyPage);

        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(clickAnalyticsService).getClicksForUrl(eq(testUrl), any(), any(), any());
    }

    @Test
    void getShortUrlClicks_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks"))
                .andExpect(status().isUnauthorized());

        verify(shortUrlService, never()).resolveShortCode(any());
        verify(authenticationService, never()).getCurrentUser();
    }

    @Test
    @WithMockUser(authorities = "SCOPE_api.write") // Wrong authority
    void getShortUrlClicks_WithoutReadAuthority_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/analytics/" + shortCode + "/clicks"))
                .andExpect(status().isForbidden());

        verify(shortUrlService, never()).resolveShortCode(any());
        verify(authenticationService, never()).getCurrentUser();
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        // This enables method-level security for the test
    }
}

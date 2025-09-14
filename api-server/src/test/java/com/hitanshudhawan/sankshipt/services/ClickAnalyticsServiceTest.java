package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.Click;
import com.hitanshudhawan.sankshipt.models.URL;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.ClickRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClickAnalyticsServiceTest {

    @Mock
    private ClickRepository clickRepository;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private ClickAnalyticsServiceImpl clickAnalyticsService;

    private URL testUrl;
    private User testUser;
    private Click testClick;
    private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private final String shortCode = "abc123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testUrl = new URL();
        testUrl.setId(1L);
        testUrl.setShortCode(shortCode);
        testUrl.setOriginalUrl("https://www.example.com");
        testUrl.setUser(testUser);

        testClick = new Click();
        testClick.setId(1L);
        testClick.setUrl(testUrl);
        testClick.setUserAgent(userAgent);
        testClick.setClickedAt(new Date());
    }

    @Test
    void recordClick_ShouldCreateAndSaveClick() {
        // Arrange
        when(httpServletRequest.getHeader("User-Agent")).thenReturn(userAgent);
        when(clickRepository.save(any(Click.class))).thenReturn(testClick);

        // Act
        Click result = clickAnalyticsService.recordClick(testUrl, httpServletRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testClick, result);
        verify(httpServletRequest).getHeader("User-Agent");
        verify(clickRepository).save(any(Click.class));
    }

    @Test
    void recordClick_WithNullUserAgent_ShouldStillCreateClick() {
        // Arrange
        when(httpServletRequest.getHeader("User-Agent")).thenReturn(null);
        
        Click clickWithNullUserAgent = new Click();
        clickWithNullUserAgent.setId(1L);
        clickWithNullUserAgent.setUrl(testUrl);
        clickWithNullUserAgent.setUserAgent(null);
        clickWithNullUserAgent.setClickedAt(new Date());
        
        when(clickRepository.save(any(Click.class))).thenReturn(clickWithNullUserAgent);

        // Act
        Click result = clickAnalyticsService.recordClick(testUrl, httpServletRequest);

        // Assert
        assertNotNull(result);
        assertNull(result.getUserAgent());
        assertEquals(testUrl, result.getUrl());
        verify(httpServletRequest).getHeader("User-Agent");
        verify(clickRepository).save(any(Click.class));
    }

    @Test
    void getClickCountForUrl_ShouldReturnCorrectCount() {
        // Arrange
        Long expectedCount = 5L;
        when(clickRepository.countByUrlShortCode(shortCode)).thenReturn(expectedCount);

        // Act
        Long result = clickAnalyticsService.getClickCountForUrl(testUrl);

        // Assert
        assertEquals(expectedCount, result);
        verify(clickRepository).countByUrlShortCode(shortCode);
    }

    @Test
    void getClickCountForUrl_NoClicks_ShouldReturnZero() {
        // Arrange
        when(clickRepository.countByUrlShortCode(shortCode)).thenReturn(0L);

        // Act
        Long result = clickAnalyticsService.getClickCountForUrl(testUrl);

        // Assert
        assertEquals(0L, result);
        verify(clickRepository).countByUrlShortCode(shortCode);
    }

    @Test
    void getClicksForUrl_WithDefaults_ShouldReturnPagedResults() {
        // Arrange
        List<Click> clickList = Arrays.asList(testClick);
        Page<Click> clickPage = new PageImpl<>(clickList);
        
        PageRequest expectedPageRequest = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("clickedAt")));
        when(clickRepository.findByUrl(eq(testUrl), eq(expectedPageRequest))).thenReturn(clickPage);

        // Act
        Page<Click> result = clickAnalyticsService.getClicksForUrl(testUrl, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testClick, result.getContent().get(0));
        verify(clickRepository).findByUrl(eq(testUrl), eq(expectedPageRequest));
    }

    @Test
    void getClicksForUrl_WithCustomParameters_ShouldReturnPagedResults() {
        // Arrange
        int pageNumber = 2;
        int pageSize = 10;
        String sortOrder = "ASC";
        
        List<Click> clickList = Arrays.asList(testClick);
        Page<Click> clickPage = new PageImpl<>(clickList);
        
        PageRequest expectedPageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("clickedAt")));
        when(clickRepository.findByUrl(eq(testUrl), eq(expectedPageRequest))).thenReturn(clickPage);

        // Act
        Page<Click> result = clickAnalyticsService.getClicksForUrl(testUrl, pageNumber, pageSize, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testClick, result.getContent().get(0));
        verify(clickRepository).findByUrl(eq(testUrl), eq(expectedPageRequest));
    }

    @Test
    void getClicksForUrl_WithDescSort_ShouldReturnPagedResults() {
        // Arrange
        int pageNumber = 0;
        int pageSize = 5;
        String sortOrder = "DESC";
        
        List<Click> clickList = Arrays.asList(testClick);
        Page<Click> clickPage = new PageImpl<>(clickList);
        
        PageRequest expectedPageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("clickedAt")));
        when(clickRepository.findByUrl(eq(testUrl), eq(expectedPageRequest))).thenReturn(clickPage);

        // Act
        Page<Click> result = clickAnalyticsService.getClicksForUrl(testUrl, pageNumber, pageSize, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testClick, result.getContent().get(0));
        verify(clickRepository).findByUrl(eq(testUrl), eq(expectedPageRequest));
    }

    @Test
    void getClicksForUrl_EmptyResults_ShouldReturnEmptyPage() {
        // Arrange
        Page<Click> emptyPage = new PageImpl<>(Arrays.asList());
        
        PageRequest expectedPageRequest = PageRequest.of(0, 20, Sort.by(Sort.Order.desc("clickedAt")));
        when(clickRepository.findByUrl(eq(testUrl), eq(expectedPageRequest))).thenReturn(emptyPage);

        // Act
        Page<Click> result = clickAnalyticsService.getClicksForUrl(testUrl, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(clickRepository).findByUrl(eq(testUrl), eq(expectedPageRequest));
    }
}

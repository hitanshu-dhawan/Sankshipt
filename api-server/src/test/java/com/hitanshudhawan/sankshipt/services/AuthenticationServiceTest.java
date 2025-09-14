package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User testUser;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(testEmail);
    }

    @Test
    void getCurrentUser_ValidJwtAuthentication_ShouldReturnUser() {
        // Arrange
        when(jwt.getSubject()).thenReturn(testEmail);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userService.getOrCreateUser(testEmail)).thenReturn(testUser);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNotNull(result);
            assertEquals(testUser, result);
            verify(userService).getOrCreateUser(testEmail);
        }
    }

    @Test
    void getCurrentUser_NoAuthentication_ShouldReturnNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNull(result);
            verify(userService, never()).getOrCreateUser(any());
        }
    }

    @Test
    void getCurrentUser_NotAuthenticated_ShouldReturnNull() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNull(result);
            verify(userService, never()).getOrCreateUser(any());
        }
    }

    @Test
    void getCurrentUser_AnonymousUser_ShouldReturnNull() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNull(result);
            verify(userService, never()).getOrCreateUser(any());
        }
    }

    @Test
    void getCurrentUser_NonJwtPrincipal_ShouldReturnNull() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("someOtherPrincipal");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNull(result);
            verify(userService, never()).getOrCreateUser(any());
        }
    }

    @Test
    void getCurrentUser_JwtWithNullSubject_ShouldReturnNull() {
        // Arrange
        when(jwt.getSubject()).thenReturn(null);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            User result = authenticationService.getCurrentUser();

            // Assert
            assertNull(result);
            verify(userService, never()).getOrCreateUser(any());
        }
    }
}

package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.exceptions.TokenNotFoundException;
import com.hitanshudhawan.sankshipt.exceptions.UserAlreadyExistsException;
import com.hitanshudhawan.sankshipt.exceptions.UserNotFoundException;
import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.Token;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.RoleRepository;
import com.hitanshudhawan.sankshipt.repositories.TokenRepository;
import com.hitanshudhawan.sankshipt.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;
    private Token testToken;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail(testEmail);
        testUser.setPassword(encodedPassword);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setValue("USER");

        testToken = new Token();
        testToken.setId(1L);
        testToken.setValue("testTokenValue123");
        testToken.setUser(testUser);
        testToken.setExpired(false);
        
        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, 30);
        testToken.setExpiryDate(future.getTime());
    }

    @Test
    void signUp_NewUser_ShouldCreateUser() throws UserAlreadyExistsException {
        // Arrange
        List<String> roleValues = Arrays.asList("USER");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByValue("USER")).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.signUp("Test", "User", testEmail, testPassword, roleValues);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).encode(testPassword);
        verify(roleRepository).findByValue("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_ExistingUser_ShouldThrowException() {
        // Arrange
        List<String> roleValues = Arrays.asList("USER");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.signUp("Test", "User", testEmail, testPassword, roleValues));

        assertEquals(testEmail, exception.getEmail());
        assertTrue(exception.getMessage().contains("User with email already exists"));
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUp_WithNonExistentRole_ShouldCreateUserWithoutInvalidRole() throws UserAlreadyExistsException {
        // Arrange
        List<String> roleValues = Arrays.asList("INVALID_ROLE");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByValue("INVALID_ROLE")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.signUp("Test", "User", testEmail, testPassword, roleValues);

        // Assert
        assertNotNull(result);
        verify(roleRepository).findByValue("INVALID_ROLE");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signIn_ValidCredentials_ShouldReturnToken() throws UserNotFoundException {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // Act
        Token result = userService.signIn(testEmail, testPassword);

        // Assert
        assertNotNull(result);
        assertEquals(testToken, result);
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void signIn_NonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.signIn(testEmail, testPassword));

        assertEquals(testEmail, exception.getEmail());
        assertTrue(exception.getMessage().contains("User with email not found"));
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void signIn_InvalidPassword_ShouldReturnNull() throws UserNotFoundException {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        // Act
        Token result = userService.signIn(testEmail, testPassword);

        // Assert
        assertNull(result);
        verify(userRepository).findByEmail(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void signOut_ValidToken_ShouldExpireToken() throws TokenNotFoundException {
        // Arrange
        String tokenValue = "validTokenValue";
        when(tokenRepository.findByValueAndIsExpired(tokenValue, false)).thenReturn(Optional.of(testToken));
        when(tokenRepository.save(testToken)).thenReturn(testToken);

        // Act
        userService.signOut(tokenValue);

        // Assert
        assertTrue(testToken.isExpired());
        verify(tokenRepository).findByValueAndIsExpired(tokenValue, false);
        verify(tokenRepository).save(testToken);
    }

    @Test
    void signOut_InvalidToken_ShouldThrowException() {
        // Arrange
        String tokenValue = "invalidTokenValue";
        when(tokenRepository.findByValueAndIsExpired(tokenValue, false)).thenReturn(Optional.empty());

        // Act & Assert
        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class,
                () -> userService.signOut(tokenValue));

        assertEquals(tokenValue, exception.getToken());
        assertTrue(exception.getMessage().contains("Token not found"));
        verify(tokenRepository).findByValueAndIsExpired(tokenValue, false);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void validateToken_ValidToken_ShouldReturnUser() throws TokenNotFoundException {
        // Arrange
        String tokenValue = "validTokenValue";
        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.DAY_OF_MONTH, 1);
        
        when(tokenRepository.findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class)))
                .thenReturn(Optional.of(testToken));

        // Act
        User result = userService.validateToken(tokenValue);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(tokenRepository).findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class));
    }

    @Test
    void validateToken_InvalidToken_ShouldThrowException() {
        // Arrange
        String tokenValue = "invalidTokenValue";
        when(tokenRepository.findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class,
                () -> userService.validateToken(tokenValue));

        assertEquals(tokenValue, exception.getToken());
        assertTrue(exception.getMessage().contains("Token not found"));
        verify(tokenRepository).findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class));
    }

    @Test
    void validateToken_ExpiredToken_ShouldThrowException() {
        // Arrange
        String tokenValue = "expiredTokenValue";
        when(tokenRepository.findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class,
                () -> userService.validateToken(tokenValue));

        assertEquals(tokenValue, exception.getToken());
        verify(tokenRepository).findByValueAndIsExpiredAndExpiryDateAfter(eq(tokenValue), eq(false), any(Date.class));
    }

    @Test
    void signUp_WithMultipleRoles_ShouldCreateUserWithAllValidRoles() throws UserAlreadyExistsException {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setValue("ADMIN");
        
        List<String> roleValues = Arrays.asList("USER", "ADMIN", "INVALID_ROLE");
        
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByValue("USER")).thenReturn(Optional.of(testRole));
        when(roleRepository.findByValue("ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByValue("INVALID_ROLE")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.signUp("Test", "User", testEmail, testPassword, roleValues);

        // Assert
        assertNotNull(result);
        verify(roleRepository).findByValue("USER");
        verify(roleRepository).findByValue("ADMIN");
        verify(roleRepository).findByValue("INVALID_ROLE");
        verify(userRepository).save(any(User.class));
    }
}

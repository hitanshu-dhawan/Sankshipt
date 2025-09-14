package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail(testEmail);
    }

    @Test
    void getOrCreateUser_ExistingUser_ShouldReturnExistingUser() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.getOrCreateUser(testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(existingUser, result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository).findByEmail(testEmail);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getOrCreateUser_NewUser_ShouldCreateAndReturnNewUser() {
        // Arrange
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail(testEmail);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.getOrCreateUser(testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(newUser, result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository).findByEmail(testEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getOrCreateUser_NullEmail_ShouldHandleGracefully() {
        // Arrange
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        
        User newUser = new User();
        newUser.setId(3L);
        newUser.setEmail(null);
        
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.getOrCreateUser(null);

        // Assert
        assertNotNull(result);
        assertEquals(newUser, result);
        assertNull(result.getEmail());
        verify(userRepository).findByEmail(null);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getOrCreateUser_EmptyEmail_ShouldCreateUserWithEmptyEmail() {
        // Arrange
        String emptyEmail = "";
        User newUser = new User();
        newUser.setId(4L);
        newUser.setEmail(emptyEmail);

        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act
        User result = userService.getOrCreateUser(emptyEmail);

        // Assert
        assertNotNull(result);
        assertEquals(newUser, result);
        assertEquals(emptyEmail, result.getEmail());
        verify(userRepository).findByEmail(emptyEmail);
        verify(userRepository).save(any(User.class));
    }
}

package com.hitanshudhawan.sankshipt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitanshudhawan.sankshipt.dtos.SignInRequestDto;
import com.hitanshudhawan.sankshipt.dtos.SignOutRequestDto;
import com.hitanshudhawan.sankshipt.dtos.SignUpRequestDto;
import com.hitanshudhawan.sankshipt.exceptions.TokenNotFoundException;
import com.hitanshudhawan.sankshipt.exceptions.UserAlreadyExistsException;
import com.hitanshudhawan.sankshipt.exceptions.UserNotFoundException;
import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.Token;
import com.hitanshudhawan.sankshipt.models.User;
import com.hitanshudhawan.sankshipt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Token testToken;
    private Role testRole;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setValue("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail(testEmail);
        testUser.setRoles(Arrays.asList(testRole));

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
    void signUp_ValidRequest_ShouldReturnUserDto() throws Exception {
        // Arrange
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setRoles(Arrays.asList("USER"));

        when(userService.signUp(anyString(), anyString(), anyString(), anyString(), anyList()))
                .thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.roles[0].value").value("USER"));

        verify(userService).signUp("Test", "User", testEmail, testPassword, Arrays.asList("USER"));
    }

    @Test
    void signUp_UserAlreadyExists_ShouldReturnConflict() throws Exception {
        // Arrange
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setRoles(Arrays.asList("USER"));

        when(userService.signUp(anyString(), anyString(), anyString(), anyString(), anyList()))
                .thenThrow(new UserAlreadyExistsException(testEmail, "User already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(userService).signUp("Test", "User", testEmail, testPassword, Arrays.asList("USER"));
    }

    @Test
    void signIn_ValidCredentials_ShouldReturnToken() throws Exception {
        // Arrange
        SignInRequestDto request = new SignInRequestDto();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        when(userService.signIn(testEmail, testPassword)).thenReturn(testToken);

        // Act & Assert
        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("testTokenValue123"))
                .andExpect(jsonPath("$.expired").value(false));

        verify(userService).signIn(testEmail, testPassword);
    }

    @Test
    void signIn_InvalidCredentials_ShouldReturnOkWithNullToken() throws Exception {
        // Arrange
        SignInRequestDto request = new SignInRequestDto();
        request.setEmail(testEmail);
        request.setPassword("wrongPassword");

        when(userService.signIn(testEmail, "wrongPassword")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService).signIn(testEmail, "wrongPassword");
    }

    @Test
    void signIn_UserNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        SignInRequestDto request = new SignInRequestDto();
        request.setEmail("nonexistent@example.com");
        request.setPassword(testPassword);

        when(userService.signIn("nonexistent@example.com", testPassword))
                .thenThrow(new UserNotFoundException("nonexistent@example.com", "User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService).signIn("nonexistent@example.com", testPassword);
    }

    @Test
    void signOut_ValidToken_ShouldReturnOk() throws Exception {
        // Arrange
        SignOutRequestDto request = new SignOutRequestDto();
        request.setToken("validTokenValue");

        doNothing().when(userService).signOut("validTokenValue");

        // Act & Assert
        mockMvc.perform(post("/api/users/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).signOut("validTokenValue");
    }

    @Test
    void signOut_InvalidToken_ShouldReturnNotFound() throws Exception {
        // Arrange
        SignOutRequestDto request = new SignOutRequestDto();
        request.setToken("invalidTokenValue");

        doThrow(new TokenNotFoundException("invalidTokenValue", "Token not found"))
                .when(userService).signOut("invalidTokenValue");

        // Act & Assert
        mockMvc.perform(post("/api/users/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(userService).signOut("invalidTokenValue");
    }

    @Test
    void validateToken_ValidToken_ShouldReturnUserDto() throws Exception {
        // Arrange
        String tokenValue = "validTokenValue";
        when(userService.validateToken(tokenValue)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/validate/" + tokenValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.roles[0].value").value("USER"));

        verify(userService).validateToken(tokenValue);
    }

    @Test
    void validateToken_InvalidToken_ShouldReturnNotFound() throws Exception {
        // Arrange
        String tokenValue = "invalidTokenValue";
        when(userService.validateToken(tokenValue))
                .thenThrow(new TokenNotFoundException(tokenValue, "Token not found"));

        // Act & Assert
        mockMvc.perform(get("/api/users/validate/" + tokenValue))
                .andExpect(status().isNotFound());

        verify(userService).validateToken(tokenValue);
    }

    @Test
    void signUp_WithEmptyRoles_ShouldReturnUserDto() throws Exception {
        // Arrange
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        request.setRoles(Arrays.asList());

        User userWithoutRoles = new User();
        userWithoutRoles.setId(1L);
        userWithoutRoles.setFirstName("Test");
        userWithoutRoles.setLastName("User");
        userWithoutRoles.setEmail(testEmail);
        userWithoutRoles.setRoles(Arrays.asList());

        when(userService.signUp(anyString(), anyString(), anyString(), anyString(), anyList()))
                .thenReturn(userWithoutRoles);

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value(testEmail))
                .andExpect(jsonPath("$.roles").isEmpty());

        verify(userService).signUp("Test", "User", testEmail, testPassword, Arrays.asList());
    }

    @Test
    void signUp_WithMultipleRoles_ShouldReturnUserDto() throws Exception {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setValue("ADMIN");

        List<Role> multipleRoles = Arrays.asList(testRole, adminRole);
        
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Admin");
        request.setLastName("User");
        request.setEmail("admin@example.com");
        request.setPassword(testPassword);
        request.setRoles(Arrays.asList("USER", "ADMIN"));

        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@example.com");
        adminUser.setRoles(multipleRoles);

        when(userService.signUp(anyString(), anyString(), anyString(), anyString(), anyList()))
                .thenReturn(adminUser);

        // Act & Assert
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Admin"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].value").value("USER"))
                .andExpect(jsonPath("$.roles[1].value").value("ADMIN"));

        verify(userService).signUp("Admin", "User", "admin@example.com", testPassword, Arrays.asList("USER", "ADMIN"));
    }
}

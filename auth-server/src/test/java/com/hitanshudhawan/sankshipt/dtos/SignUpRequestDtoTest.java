package com.hitanshudhawan.sankshipt.dtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestDtoTest {

    private SignUpRequestDto signUpRequest;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequestDto();
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String password = "password123";
        List<String> roles = Arrays.asList("USER", "ADMIN");

        // Act
        signUpRequest.setFirstName(firstName);
        signUpRequest.setLastName(lastName);
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setRoles(roles);

        // Assert
        assertEquals(firstName, signUpRequest.getFirstName());
        assertEquals(lastName, signUpRequest.getLastName());
        assertEquals(email, signUpRequest.getEmail());
        assertEquals(password, signUpRequest.getPassword());
        assertEquals(roles, signUpRequest.getRoles());
    }

    @Test
    void constructor_ShouldCreateEmptyObject() {
        // Assert
        assertNull(signUpRequest.getFirstName());
        assertNull(signUpRequest.getLastName());
        assertNull(signUpRequest.getEmail());
        assertNull(signUpRequest.getPassword());
        assertNull(signUpRequest.getRoles());
    }

    @Test
    void setFirstName_WithNull_ShouldAcceptNull() {
        // Act
        signUpRequest.setFirstName(null);

        // Assert
        assertNull(signUpRequest.getFirstName());
    }

    @Test
    void setLastName_WithNull_ShouldAcceptNull() {
        // Act
        signUpRequest.setLastName(null);

        // Assert
        assertNull(signUpRequest.getLastName());
    }

    @Test
    void setEmail_WithNull_ShouldAcceptNull() {
        // Act
        signUpRequest.setEmail(null);

        // Assert
        assertNull(signUpRequest.getEmail());
    }

    @Test
    void setPassword_WithNull_ShouldAcceptNull() {
        // Act
        signUpRequest.setPassword(null);

        // Assert
        assertNull(signUpRequest.getPassword());
    }

    @Test
    void setRoles_WithNull_ShouldAcceptNull() {
        // Act
        signUpRequest.setRoles(null);

        // Assert
        assertNull(signUpRequest.getRoles());
    }

    @Test
    void setRoles_WithEmptyList_ShouldAcceptEmptyList() {
        // Arrange
        List<String> emptyRoles = Arrays.asList();

        // Act
        signUpRequest.setRoles(emptyRoles);

        // Assert
        assertEquals(emptyRoles, signUpRequest.getRoles());
        assertTrue(signUpRequest.getRoles().isEmpty());
    }

    @Test
    void setRoles_WithSingleRole_ShouldAcceptSingleRole() {
        // Arrange
        List<String> singleRole = Arrays.asList("USER");

        // Act
        signUpRequest.setRoles(singleRole);

        // Assert
        assertEquals(singleRole, signUpRequest.getRoles());
        assertEquals(1, signUpRequest.getRoles().size());
        assertEquals("USER", signUpRequest.getRoles().get(0));
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Arrange
        SignUpRequestDto request1 = new SignUpRequestDto();
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setEmail("john@example.com");
        request1.setPassword("password");
        request1.setRoles(Arrays.asList("USER"));

        SignUpRequestDto request2 = new SignUpRequestDto();
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setEmail("john@example.com");
        request2.setPassword("password");
        request2.setRoles(Arrays.asList("USER"));

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        SignUpRequestDto request1 = new SignUpRequestDto();
        request1.setFirstName("John");
        request1.setEmail("john@example.com");

        SignUpRequestDto request2 = new SignUpRequestDto();
        request2.setFirstName("Jane");
        request2.setEmail("jane@example.com");

        // Assert
        assertNotEquals(request1, request2);
    }

    @Test
    void toString_ShouldIncludeFieldValues() {
        // Arrange
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setEmail("john@example.com");
        signUpRequest.setPassword("testPassword123");
        signUpRequest.setRoles(Arrays.asList("USER"));

        // Act
        String toString = signUpRequest.toString();

        // Assert
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("USER"));
        // Note: Lombok @Data includes all fields in toString, including password
        // In production, consider using @ToString(exclude = "password") for security
        assertTrue(toString.contains("password"));
    }
}

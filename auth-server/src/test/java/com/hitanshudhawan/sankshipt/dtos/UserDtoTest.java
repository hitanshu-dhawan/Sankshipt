package com.hitanshudhawan.sankshipt.dtos;

import com.hitanshudhawan.sankshipt.models.Role;
import com.hitanshudhawan.sankshipt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        Role role = new Role();
        role.setValue("USER");
        List<Role> roles = Arrays.asList(role);

        // Act
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setRoles(roles);

        // Assert
        assertEquals(firstName, userDto.getFirstName());
        assertEquals(lastName, userDto.getLastName());
        assertEquals(email, userDto.getEmail());
        assertEquals(roles, userDto.getRoles());
    }

    @Test
    void constructor_ShouldCreateEmptyObject() {
        // Assert
        assertNull(userDto.getFirstName());
        assertNull(userDto.getLastName());
        assertNull(userDto.getEmail());
        assertNull(userDto.getRoles());
    }

    @Test
    void fromUser_WithCompleteUser_ShouldCreateCorrectDto() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setValue("USER");

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setRoles(Arrays.asList(role));

        // Act
        UserDto result = UserDto.fromUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(1, result.getRoles().size());
        assertEquals("USER", result.getRoles().get(0).getValue());
    }

    @Test
    void fromUser_WithNullValues_ShouldCreateDtoWithNullValues() {
        // Arrange
        User user = new User();
        user.setFirstName(null);
        user.setLastName(null);
        user.setEmail(null);
        user.setRoles(null);

        // Act
        UserDto result = UserDto.fromUser(user);

        // Assert
        assertNotNull(result);
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getEmail());
        assertNull(result.getRoles());
    }

    @Test
    void fromUser_WithEmptyRoles_ShouldCreateDtoWithEmptyRoles() {
        // Arrange
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setRoles(Arrays.asList());

        // Act
        UserDto result = UserDto.fromUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertNotNull(result.getRoles());
        assertTrue(result.getRoles().isEmpty());
    }

    @Test
    void fromUser_WithMultipleRoles_ShouldCreateDtoWithAllRoles() {
        // Arrange
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setValue("USER");

        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setValue("ADMIN");

        User user = new User();
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail("admin@example.com");
        user.setRoles(Arrays.asList(userRole, adminRole));

        // Act
        UserDto result = UserDto.fromUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("Admin", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals(2, result.getRoles().size());
        assertEquals("USER", result.getRoles().get(0).getValue());
        assertEquals("ADMIN", result.getRoles().get(1).getValue());
    }

    @Test
    void equals_WithSameValues_ShouldBeEqual() {
        // Arrange
        Role role = new Role();
        role.setValue("USER");

        UserDto dto1 = new UserDto();
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setEmail("john@example.com");
        dto1.setRoles(Arrays.asList(role));

        UserDto dto2 = new UserDto();
        dto2.setFirstName("John");
        dto2.setLastName("Doe");
        dto2.setEmail("john@example.com");
        dto2.setRoles(Arrays.asList(role));

        // Assert
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void equals_WithDifferentValues_ShouldNotBeEqual() {
        // Arrange
        UserDto dto1 = new UserDto();
        dto1.setFirstName("John");
        dto1.setEmail("john@example.com");

        UserDto dto2 = new UserDto();
        dto2.setFirstName("Jane");
        dto2.setEmail("jane@example.com");

        // Assert
        assertNotEquals(dto1, dto2);
    }

    @Test
    void toString_ShouldIncludeFieldValues() {
        // Arrange
        Role role = new Role();
        role.setValue("USER");

        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john@example.com");
        userDto.setRoles(Arrays.asList(role));

        // Act
        String toString = userDto.toString();

        // Assert
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("john@example.com"));
    }
}

package com.ecom.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecom.userservice.entity.Role;
import com.ecom.userservice.entity.RoleName;
import com.ecom.userservice.entity.UserAccount;
import com.ecom.userservice.repository.UserAccountRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAccountDetailsService Tests")
class UserAccountDetailsServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountDetailsService userAccountDetailsService;

    private UserAccount userAccount;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.USER);

        userAccount = new UserAccount();
        userAccount.setId(1L);
        userAccount.setUsername("testuser");
        userAccount.setPasswordHash("$2a$10$encodedPassword");
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        userAccount.setRoles(roles);
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void testLoadUserByUsername_Success() {
        // Arrange
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        // Act
        UserDetails userDetails = userAccountDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userAccountRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should load user with ADMIN role successfully")
    void testLoadUserByUsername_AdminRole() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(RoleName.ADMIN);
        
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        
        UserAccount adminAccount = new UserAccount();
        adminAccount.setId(2L);
        adminAccount.setUsername("admin");
        adminAccount.setPasswordHash("$2a$10$encodedPassword");
        adminAccount.setRoles(adminRoles);
        
        when(userAccountRepository.findByUsername("admin")).thenReturn(Optional.of(adminAccount));

        // Act
        UserDetails userDetails = userAccountDetailsService.loadUserByUsername("admin");

        // Assert
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userAccountRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Should load user with multiple roles successfully")
    void testLoadUserByUsername_MultipleRoles() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName(RoleName.ADMIN);
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        roles.add(adminRole);
        userAccount.setRoles(roles);
        
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        // Act
        UserDetails userDetails = userAccountDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(userAccountRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsername_NotFound() {
        // Arrange
        when(userAccountRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userAccountDetailsService.loadUserByUsername("nonexistent")
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userAccountRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should load user with no roles (empty role set)")
    void testLoadUserByUsername_NoRoles() {
        // Arrange
        userAccount.setRoles(new HashSet<>());
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        // Act
        UserDetails userDetails = userAccountDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals(0, userDetails.getAuthorities().size());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        verify(userAccountRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should return correct password hash")
    void testLoadUserByUsername_PasswordHash() {
        // Arrange
        userAccount.setPasswordHash("$2a$12$anotherEncodedPassword");
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        // Act
        UserDetails userDetails = userAccountDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("$2a$12$anotherEncodedPassword", userDetails.getPassword());
        verify(userAccountRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void testLoadUserByUsername_NullUsername() {
        // Arrange
        when(userAccountRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            UsernameNotFoundException.class,
            () -> userAccountDetailsService.loadUserByUsername(null)
        );
        verify(userAccountRepository, times(1)).findByUsername(null);
    }

    @Test
    @DisplayName("Should handle empty username gracefully")
    void testLoadUserByUsername_EmptyUsername() {
        // Arrange
        when(userAccountRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            UsernameNotFoundException.class,
            () -> userAccountDetailsService.loadUserByUsername("")
        );
        verify(userAccountRepository, times(1)).findByUsername("");
    }
}


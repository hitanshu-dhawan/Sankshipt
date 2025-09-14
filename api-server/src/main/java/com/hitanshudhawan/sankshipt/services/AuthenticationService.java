package com.hitanshudhawan.sankshipt.services;

import com.hitanshudhawan.sankshipt.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets the current authenticated user from the security context
     * @return the current User or null if not authenticated
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        // In OAuth2 resource server, the principal is a Jwt
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getSubject(); // The subject is typically the email in our setup
            if (email != null) {
                return userService.getOrCreateUser(email);
            }
        }

        return null;
    }

}

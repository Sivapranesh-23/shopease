package com.shopease.security;

import com.shopease.domain.User;
import com.shopease.security.jwt.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Convenience accessor for the currently authenticated principal.
 */
@Component
public class SecurityUtils {

    public Optional<AuthenticatedUser> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public AuthenticatedUser requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("No authenticated user in security context"));
    }

    public Long requireCurrentUserId() {
        return requireCurrentUser().id();
    }

    public User requireCurrentDomainUser() {
        return requireCurrentUser().domainUser();
    }
}

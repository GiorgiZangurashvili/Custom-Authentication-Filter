package org.example.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.config.security.authentication.CustomAuthentication;
import org.example.config.security.manager.CustomAuthManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Instead of implementing Filter interface we extend OncePerRequestFilter class.
 * Reason: We want this filter to be applied once.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthFilter extends OncePerRequestFilter {
    private final CustomAuthManager authManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. create an authentication object which is not yet authenticated
        // 2. delegate the authentication object to the manager
        // 3. get back the authentication from the manager
        // 4. if the object is authenticated then send request to the next filter in the chain
        String key = String.valueOf(request.getHeader("key"));
        var customAuthentication = new CustomAuthentication(false, key);

        var authentication = authManager.authenticate(customAuthentication);

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response); // Only when authentication worked
        }
    }
}

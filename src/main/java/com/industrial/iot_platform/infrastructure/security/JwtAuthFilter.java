package com.industrial.iot_platform.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String deviceId;

        // 1. Check if the header contains JWT Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract JWT Token
        jwt = authHeader.substring(7);

        try {
            deviceId = jwtService.extractUsername(jwt);

            // 3. If Device ID exists and not already authenticated
            if (deviceId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 4. Create Authentication Object
                // Assign role "DEVICE" to every authenticated machine

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        deviceId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEVICE"))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Context is updated! The user is now logged in.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Token is invalid/expired.
            System.err.println("JWT Validation Failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

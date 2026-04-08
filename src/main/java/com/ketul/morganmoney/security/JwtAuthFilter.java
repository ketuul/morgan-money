package com.ketul.morganmoney.security;

import com.ketul.morganmoney.model.User;
import com.ketul.morganmoney.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

// This filter runs on every single HTTP request before it reaches your controller
// It checks if there's a valid JWT token in the Authorization header
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Look for the Authorization header — it looks like: "Bearer eyJhbGc..."
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token — let the request through, Spring Security will block it if needed
            filterChain.doFilter(request, response);
            return;
        }

        // Strip the "Bearer " prefix to get just the token
        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token is valid — extract the email and look up the user
        String email = jwtService.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            // Tell Spring Security this request is authenticated
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.get(), null, new ArrayList<>());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

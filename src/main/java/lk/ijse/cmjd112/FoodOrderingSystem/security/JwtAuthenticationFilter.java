package lk.ijse.cmjd112.FoodOrderingSystem.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JWT authentication filter responsible for validating JWT tokens included in incoming HTTP requests.

 * This filter executes once per request and performs:
 
 *     JWT token extraction from Authorization header
 *     Token validation
 *     User authentication
 *     Security context population

 * Valid JWT tokens are converted into authenticated Spring Security authentication objects and stored in the SecurityContext.
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Service responsible for JWT token operations.
     */
    private final JwtService jwtService;

    /**
     * Service used to load authenticated user details.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Filters and validates incoming JWT authentication requests.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Retrieve Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // Skip authentication if Authorization header is missing
        // or does not contain a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token by removing "Bearer " prefix
        String token = authHeader.substring(7);

        // Extract username (email) from JWT token
        String username = jwtService.extractUsername(token);

        // Authenticate only if username exists and user is not already authenticated
        if (
                username != null
                        && SecurityContextHolder.getContext()
                        .getAuthentication() == null
        ) {

            // Load user details from database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // Validate JWT token against user details
            if (jwtService.isTokenValid(token, userDetails)) {

                // Create authenticated token object
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request-specific authentication details
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Store authentication inside Spring Security context
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }

        // Continue request processing through remaining filters
        filterChain.doFilter(request, response);
    }
}
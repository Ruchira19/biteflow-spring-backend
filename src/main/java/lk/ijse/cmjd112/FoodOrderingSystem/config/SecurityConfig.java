package lk.ijse.cmjd112.FoodOrderingSystem.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lk.ijse.cmjd112.FoodOrderingSystem.security.CustomUserDetailsService;
import lk.ijse.cmjd112.FoodOrderingSystem.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * Handles application security configuration.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT auth filter used to validate JWT tokens before processing secured requests.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Custom user details service used for loading user specific data.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Configures the main Spring Security filter chain.
     *
     * @param http HttpSecurity configuration object
     * returns configured SecurityFilterChain
     * throw an exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF for stateless REST APIs
                .csrf(csrf -> csrf.disable())

                // Enable CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure stateless session management for JWT authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure endpoint authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Public GET endpoints
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/categories/**",
                                "/api/v1/foods/**"
                        ).permitAll()

                        // Allow access to Spring error endpoint
                        .requestMatchers("/error").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before Spring authentication filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        // DAO authentication provider for username/password authentication
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        // Configure password encoder
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
    /**
     * Exposes AuthenticationManager bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     * returns PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS settings for the application.
     * returns configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests only from the frontend application
        configuration.setAllowedOrigins(
                List.of("http://localhost:3000")
        );

        // Allow all request headers such as Authorization and Content-Type
        configuration.setAllowedHeaders(List.of("*"));

        // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.)
        configuration.setAllowedMethods(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply CORS configuration to all API endpoints
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
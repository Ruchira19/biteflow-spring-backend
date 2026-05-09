package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.AuthRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.AuthResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.RegisterRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.service.AuthService;
import lombok.RequiredArgsConstructor;

/**
 * This controller provides endpoints for: User sign in and signup
 * Successful authentication returns a JWT token which is then used to access protected API endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Service layer responsible for authentication logic.
     */
    private final AuthService authService;

    /**
     * Registers a new user in the system.
     * Validates user input, creates the user account, and returns a JWT authentication response.
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return authService.register(request);
    }

    /**
     * Authenticates an existing user.
     * Validates login credentials and returns a JWT token upon successful authentication.
     */
    @PostMapping("/signin")
    public AuthResponse login(
            @Valid @RequestBody AuthRequest request
    ) {

        return authService.login(request);
    }
}
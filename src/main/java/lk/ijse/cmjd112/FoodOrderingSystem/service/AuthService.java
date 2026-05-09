package lk.ijse.cmjd112.FoodOrderingSystem.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.AuthRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.AuthResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.auth.RegisterRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Cart;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.User;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.CartRepository;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.UserRepository;
import lk.ijse.cmjd112.FoodOrderingSystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for user authentication and registration operations.
 * Handles user account creation, login authentication, password encryption,
 * JWT token generation, and customer cart initialization.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    /**
     * Repository for user database operations.
     */
    private final UserRepository userRepository;

    /**
     * Repository for shopping cart database operations.
     */
    private final CartRepository cartRepository;

    /**
     * Password encoder used for secure password hashing.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentication manager used for validating login credentials.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Service responsible for JWT token generation and validation.
     */
    private final JwtService jwtService;

    /**
     * Registers a new customer account in the system.
     * Validates email uniqueness, encrypts the password,
     * creates the user account, and initializes a shopping cart.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);

        savedUser.setCart(cart);

        cartRepository.save(cart);

        log.info("Registered user {}", savedUser.getEmail());

        return buildAuthResponse(savedUser);
    }

    /**
     * Authenticates an existing user using email and password.
     * Generates a JWT token after successful authentication.
     */
    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmailWithCart(request.email())
                .orElseThrow(() ->
                        new BadRequestException("Invalid email or password")
                );

        log.info("User logged in {}", user.getEmail());

        return buildAuthResponse(user);
    }

    /**
     * Builds the authentication response returned after
     * successful registration or login.
     */
    private AuthResponse buildAuthResponse(User user) {

        return new AuthResponse(
                jwtService.generateToken(user),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
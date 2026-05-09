package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.user.AdminUserRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.user.UserResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Cart;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.User;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.CartRepository;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for user management operations.
 * Handles user creation, retrieval,
 * deletion, authentication-related lookups,
 * and user response mapping.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

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
     * Retrieves all users in the system.
     */
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .toList();
    }

    /**
     * Retrieves profile information for the authenticated user.
     */
    public UserResponse getUserProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return mapUser(user);
    }

    /**
     * Retrieves a user entity using email address.
     */
    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    /**
     * Creates a new user account.
     * Validates email uniqueness,
     * encrypts the password,
     * and initializes a shopping cart.
     */
    @Transactional
    public UserResponse createUser(
            AdminUserRequest request
    ) {

        if (userRepository.existsByEmail(request.email())) {

            throw new BadRequestException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setFullName(request.fullName());
        user.setEmail(request.email());

        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole(request.role());

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();

        cart.setUser(savedUser);

        savedUser.setCart(cart);

        cartRepository.save(cart);

        log.info(
                "Admin created user {}",
                savedUser.getEmail()
        );

        return mapUser(savedUser);
    }

    /**
     * Deletes a user account from the system.
     * Prevents administrators from deleting
     * their own account and ensures at least
     * one administrator account remains.
     */
    @Transactional
    public void deleteUser(
            String adminEmail,
            Long userId
    ) {

        User admin = findUserByEmail(adminEmail);

        User user = userRepository.findWithRelationsById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (admin.getId().equals(user.getId())) {

            throw new BadRequestException(
                    "You cannot delete your own account"
            );
        }

        if (
                user.getRole() == Role.ADMIN
                        && userRepository.countByRole(Role.ADMIN) <= 1
        ) {

            throw new BadRequestException(
                    "At least one admin account must remain"
            );
        }

        userRepository.delete(user);

        log.info(
                "Admin {} deleted user {}",
                admin.getEmail(),
                user.getEmail()
        );
    }

    /**
     * Converts user entity data into API response format.
     */
    private UserResponse mapUser(User user) {

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
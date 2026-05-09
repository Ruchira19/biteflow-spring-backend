package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.user.AdminUserRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.user.UserResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * This controller provides endpoints for:
 *     Retrieving all system users
 *     Creating new users
 *     Deleting users
 *     Retrieving the authenticated user's profile
 * Administrative operations are restricted to users with the ADMIN role, while authenticated users can access their own profile information.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Service layer responsible for user-related business logic.
     */
    private final UserService userService;

    /**
     * Retrieves all users in the system. Access restricted to ADMIN users only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    /**
     * Creates a new system user.Access restricted to ADMIN users only.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody AdminUserRequest request
    ) {

        return userService.createUser(request);
    }

    /**
     * Deletes an existing user from the system. -Access restricted to ADMIN users only.
     * Administrators are prevented from deleting restricted or unauthorized accounts through service-layer validation.
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // Need to drop orders as well from the database
    public void deleteUser(
            Principal principal,
            @PathVariable Long userId
    ) {

        userService.deleteUser(
                principal.getName(),
                userId
        );
    }

    /**
     * Retrieves the authenticated user's profile information.
     */
    @GetMapping("/me")
    public UserResponse getProfile(Principal principal) {

        return userService.getUserProfile(
                principal.getName()
        );
    }
}
package lk.ijse.cmjd112.FoodOrderingSystem.dto.auth;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;

/**
 * DTO for authentication response after successful login/signup.
 * Contains JWT token, user details, and role information.
 */
public record AuthResponse(
        String token,
        Long userId,
        String fullName,
        String email,
        Role role
) {
}

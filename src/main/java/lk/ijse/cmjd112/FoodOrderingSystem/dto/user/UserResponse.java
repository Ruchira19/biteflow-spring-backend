package lk.ijse.cmjd112.FoodOrderingSystem.dto.user;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;

/**
 * DTO for user profile information response.
 * Returns user details including ID, name, email, and role.
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role
) {
}

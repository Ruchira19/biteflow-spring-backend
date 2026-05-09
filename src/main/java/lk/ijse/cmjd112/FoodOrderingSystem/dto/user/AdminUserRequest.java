package lk.ijse.cmjd112.FoodOrderingSystem.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;

/**
 * DTO for admin user creation request.
 * Admin specifies user details including role (ADMIN or CUSTOMER).
 */
public record AdminUserRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Role role
) {
}

package lk.ijse.cmjd112.FoodOrderingSystem.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user registration request.
 * Contains new user details including full name, email, and password.
 */
public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}

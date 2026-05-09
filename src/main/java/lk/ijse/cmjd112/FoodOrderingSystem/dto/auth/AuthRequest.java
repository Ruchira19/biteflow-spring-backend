package lk.ijse.cmjd112.FoodOrderingSystem.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login request.
 * Contains email and password credentials for authentication.
 */
public record AuthRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}

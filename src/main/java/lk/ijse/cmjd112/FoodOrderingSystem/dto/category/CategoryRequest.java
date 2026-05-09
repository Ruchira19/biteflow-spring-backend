package lk.ijse.cmjd112.FoodOrderingSystem.dto.category;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for category creation/update request.
 * Contains category name and description for food item organization.
 */
public record CategoryRequest(
        @NotBlank String name,
        String description
) {
}

package lk.ijse.cmjd112.FoodOrderingSystem.dto.category;

/**
 * DTO for category information response.
 * Returns category details with ID, name, and description.
 */
public record CategoryResponse(
        Long id,
        String name,
        String description
) {
}

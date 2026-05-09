package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.food.FoodItemRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.food.FoodItemResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.service.FoodItemService;
import lombok.RequiredArgsConstructor;

/**
 * This controller provides endpoints for:
 *     Retrieving food items
 *     Filtering available food items
 *     Creating food items
 *     Updating food items
 *     Deleting food items
 * Read operations are publicly accessible, while modification operations are restricted to users with the ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {

    /**
     * Service layer responsible for food item business logic.
     */
    private final FoodItemService foodItemService;

    /**
     * Retrieves food items from the system.

         * Logic:
         * If {food availableOnly=true},
         * only available food items are returned.
         * Otherwise, all food items are retrieved.
     *
     * This endpoint is publicly accessible.
     */
    @GetMapping
    public List<FoodItemResponse> getFoodItems(
            @RequestParam(defaultValue = "false") boolean availableOnly
    ) {

        return availableOnly
                ? foodItemService.getAvailableFoodItems()
                : foodItemService.getAllFoodItems();
    }

    /**
     * Creates a new food item - Access restricted to ADMIN users only.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public FoodItemResponse createFoodItem(
            @Valid @RequestBody FoodItemRequest request
    ) {

        return foodItemService.createFoodItem(request);
    }

    /**
     * Updates an existing food item. - Access restricted to ADMIN users only.
     */
    @PutMapping("/{foodItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public FoodItemResponse updateFoodItem(
            @PathVariable Long foodItemId,
            @Valid @RequestBody FoodItemRequest request
    ) {

        return foodItemService.updateFoodItem(
                foodItemId,
                request
        );
    }

    /**
     * Deletes a food item from the system. - Access restricted to ADMIN users only.
     */
    @DeleteMapping("/{foodItemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodItem(@PathVariable Long foodItemId) {

        foodItemService.deleteFoodItem(foodItemId);
    }
}
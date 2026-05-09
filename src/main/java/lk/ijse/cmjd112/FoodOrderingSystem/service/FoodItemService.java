package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.food.FoodItemRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.food.FoodItemResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for food item management operations.
 * Handles food item creation, retrieval, updating,
 * deletion, stock management, and availability synchronization.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FoodItemService {

    /**
     * Repository for food item database operations.
     */
    private final FoodItemRepository foodItemRepository;

    /**
     * Service responsible for category-related operations.
     */
    private final CategoryService categoryService;

    /**
     * Retrieves all food items from the system.
     */
    public List<FoodItemResponse> getAllFoodItems() {

        return foodItemRepository.findAll()
                .stream()
                .map(this::mapFood)
                .toList();
    }

    /**
     * Retrieves all available food items.
     */
    public List<FoodItemResponse> getAvailableFoodItems() {

        return foodItemRepository.findByStatus(FoodStatus.AVAILABLE)
                .stream()
                .map(this::mapFood)
                .toList();
    }

    /**
     * Creates a new food item in the system.
     */
    public FoodItemResponse createFoodItem(
            FoodItemRequest request
    ) {

        FoodItem foodItem = new FoodItem();

        applyRequest(foodItem, request);

        FoodItem savedFood =
                foodItemRepository.save(foodItem);

        log.info("Created food item {}", savedFood.getName());

        return mapFood(savedFood);
    }

    /**
     * Updates an existing food item.
     */
    public FoodItemResponse updateFoodItem(
            Long foodItemId,
            FoodItemRequest request
    ) {

        FoodItem foodItem =
                getFoodItemEntity(foodItemId);

        applyRequest(foodItem, request);

        FoodItem savedFood =
                foodItemRepository.save(foodItem);

        log.info("Updated food item {}", savedFood.getName());

        return mapFood(savedFood);
    }

    /**
     * Deletes a food item from the system.
     */
    public void deleteFoodItem(Long foodItemId) {

        FoodItem foodItem =
                getFoodItemEntity(foodItemId);

        foodItemRepository.delete(foodItem);

        log.info("Deleted food item {}", foodItem.getName());
    }

    /**
     * Retrieves a food item entity using its identifier.
     */
    public FoodItem getFoodItemEntity(Long foodItemId) {

        return foodItemRepository.findById(foodItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Food item not found"
                        )
                );
    }

    /**
     * Retrieves a food item entity with database locking support.
     * Used during stock-sensitive update operations.
     */
    public FoodItem getFoodItemEntityForUpdate(
            Long foodItemId
    ) {

        return foodItemRepository.findWithLockById(foodItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Food item not found"
                        )
                );
    }

    /**
     * Applies request data to the food item entity.
     * Updates food item details, stock quantity,
     * availability status, and category assignment.
     */
    private void applyRequest(
            FoodItem foodItem,
            FoodItemRequest request
    ) {

        foodItem.setName(request.name());
        foodItem.setDescription(request.description());
        foodItem.setPrice(request.price());
        foodItem.setStockQuantity(request.stockQuantity());

        foodItem.setStatus(
                resolveStatus(
                        request.stockQuantity(),
                        request.status()
                )
        );

        foodItem.setCategory(
                categoryService.getCategoryEntity(
                        request.categoryId()
                )
        );
    }

    /**
     * Converts food item entity data into API response format.
     */
    private FoodItemResponse mapFood(FoodItem foodItem) {

        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getPrice(),
                foodItem.getStockQuantity(),
                foodItem.getStatus(),
                foodItem.getCategory().getId(),
                foodItem.getCategory().getName()
        );
    }

    /**
     * Synchronizes food item availability status
     * based on current stock quantity.
     */
    public void syncAvailability(FoodItem foodItem) {

        foodItem.setStatus(
                foodItem.getStockQuantity() > 0
                        ? FoodStatus.AVAILABLE
                        : FoodStatus.OUT_OF_STOCK
        );

        foodItemRepository.save(foodItem);
    }

    /**
     * Resolves the correct food availability status
     * based on stock quantity and requested status.
     */
    private FoodStatus resolveStatus(
            Integer stockQuantity,
            FoodStatus requestedStatus
    ) {

        if (stockQuantity != null && stockQuantity <= 0) {
            return FoodStatus.OUT_OF_STOCK;
        }

        return requestedStatus == FoodStatus.OUT_OF_STOCK
                ? FoodStatus.OUT_OF_STOCK
                : FoodStatus.AVAILABLE;
    }
}
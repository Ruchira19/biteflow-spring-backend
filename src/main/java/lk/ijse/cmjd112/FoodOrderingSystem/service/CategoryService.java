package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.category.CategoryRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.category.CategoryResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Category;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for food category management operations.
 * Handles category creation, retrieval, updating,
 * deletion, and category response mapping.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    /**
     * Repository for category database operations.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Retrieves all food categories from the system.
     */
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapCategory)
                .toList();
    }

    /**
     * Creates a new food category.
     * Validates category uniqueness before saving.
     */
    public CategoryResponse createCategory(
            CategoryRequest request
    ) {

        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Category already exists");
        }

        Category category = new Category();

        category.setName(request.name());
        category.setDescription(request.description());

        Category savedCategory =
                categoryRepository.save(category);

        log.info(
                "Created category {}",
                savedCategory.getName()
        );

        return mapCategory(savedCategory);
    }

    /**
     * Updates an existing food category.
     * Validates duplicate category names before updating.
     */
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryRequest request
    ) {

        Category category = getCategoryEntity(categoryId);

        categoryRepository.findByNameIgnoreCase(request.name())
                .filter(existing ->
                        !existing.getId().equals(categoryId)
                )
                .ifPresent(existing -> {
                    throw new BadRequestException(
                            "Category already exists"
                    );
                });

        category.setName(request.name());
        category.setDescription(request.description());

        log.info("Updated category {}", category.getName());

        return mapCategory(
                categoryRepository.save(category)
        );
    }

    /**
     * Deletes a food category from the system.
     */
    public void deleteCategory(Long categoryId) {

        Category category = getCategoryEntity(categoryId);

        categoryRepository.delete(category);

        log.info("Deleted category {}", category.getName());
    }

    /**
     * Retrieves a category entity using its identifier.
     */
    public Category getCategoryEntity(Long categoryId) {

        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"
                        )
                );
    }

    /**
     * Converts category entity data into API response format.
     */
    private CategoryResponse mapCategory(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
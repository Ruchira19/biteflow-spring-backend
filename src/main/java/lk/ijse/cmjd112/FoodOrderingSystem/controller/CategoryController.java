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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.category.CategoryRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.category.CategoryResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.service.CategoryService;
import lombok.RequiredArgsConstructor;

/**
 
 * This controller provides endpoints for:
 *     Retrieving food categories
 *     Creating new categories
 *     Updating existing categories
 *     Deleting categories
 * Category retrieval is publicly accessible, while modification operations are restricted to users with the ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    /**
     * Service layer responsible for category-related business logic.
     */
    private final CategoryService categoryService;

    /**
     * Retrieves all available food categories.
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {

        return categoryService.getAllCategories();
    }

    /**
     * Creates a new food category.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        return categoryService.createCategory(request);
    }

    /**
     * Updates an existing food category.
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request
    ) {

        return categoryService.updateCategory(
                categoryId,
                request
        );
    }

    /**
     * Deletes a food category from the system.
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {

        categoryService.deleteCategory(categoryId);
    }
}
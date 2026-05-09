package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

/**
 * Repository for FoodItem entity data access.
 * Provides CRUD operations and queries for food item retrieval by status and category.
 */
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    List<FoodItem> findAll();

    @EntityGraph(attributePaths = "category")
    List<FoodItem> findByStatus(FoodStatus status);

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<FoodItem> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FoodItem> findWithLockById(Long id);
}

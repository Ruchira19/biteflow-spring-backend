package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 * Repository for FoodOrder entity data access.
 * Provides CRUD operations and queries for order retrieval by user and status.
 */
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "items", "items.foodItem"})
    List<FoodOrder> findAll();

    @Override
    @EntityGraph(attributePaths = {"user", "items", "items.foodItem"})
    Optional<FoodOrder> findById(Long id);

    @EntityGraph(attributePaths = {"user", "items", "items.foodItem"})
    List<FoodOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
}

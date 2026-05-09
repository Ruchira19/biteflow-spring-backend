package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Cart entity data access.
 * Provides queries for retrieving user shopping carts.
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"user", "items", "items.foodItem"})
    Optional<Cart> findByUserId(Long userId);
}

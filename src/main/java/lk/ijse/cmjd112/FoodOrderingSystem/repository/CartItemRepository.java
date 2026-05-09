package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for CartItem entity data access.
 * Provides queries for finding items in shopping carts.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndFoodItemId(Long cartId, Long foodItemId);
}

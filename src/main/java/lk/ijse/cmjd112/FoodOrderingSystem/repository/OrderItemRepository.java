package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for OrderItem entity data access.
 * Provides CRUD operations for order line items.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}

package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Payment entity data access.
 * Provides queries for retrieving payment records by order.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
}

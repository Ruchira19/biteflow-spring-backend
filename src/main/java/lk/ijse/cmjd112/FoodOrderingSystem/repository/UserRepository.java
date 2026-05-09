package lk.ijse.cmjd112.FoodOrderingSystem.repository;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.User;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for User entity data access.
 * Provides CRUD operations and custom queries for user lookup and management.
 * Includes methods for finding users by email with eager loading of relationships.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"cart"})
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailWithCart(@Param("email") String email);

    @EntityGraph(attributePaths = {"cart", "cart.items", "orders", "orders.items", "orders.payment"})
    Optional<User> findWithRelationsById(Long id);

    boolean existsByEmail(String email);

    long countByRole(Role role);
}

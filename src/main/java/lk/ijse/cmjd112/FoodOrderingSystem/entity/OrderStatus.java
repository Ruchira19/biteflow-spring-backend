package lk.ijse.cmjd112.FoodOrderingSystem.entity;

/**
 * Enum for order processing status.
 * PLACED: Order has been created.
 * PREPARING: Kitchen is preparing the order.
 * DELIVERED: Order has been completed and delivered.
 * CANCELLED: Order has been cancelled.
 */
public enum OrderStatus {
    PLACED,
    PREPARING,
    DELIVERED,
    CANCELLED
}

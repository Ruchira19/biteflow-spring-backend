package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.OrderItemResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.OrderResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.PlaceOrderRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Cart;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.CartItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodOrder;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Payment;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.PaymentStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.User;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.FoodOrderRepository;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for food order management operations.
 * Handles order placement, order retrieval,
 * order cancellation, stock management,
 * payment initialization, and order status updates.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    /**
     * Repository for order database operations.
     */
    private final FoodOrderRepository foodOrderRepository;

    /**
     * Repository for payment database operations.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Service responsible for user-related operations.
     */
    private final UserService userService;

    /**
     * Service responsible for shopping cart operations.
     */
    private final CartService cartService;

    /**
     * Service responsible for food item operations.
     */
    private final FoodItemService foodItemService;

    /**
     * Places a new food order for the authenticated user.
     * Validates stock availability, creates order items,
     * updates inventory quantities, initializes payment records,
     * and clears the shopping cart after successful order placement.
     */
    @Transactional
    public OrderResponse placeOrder(
            String email,
            PlaceOrderRequest request
    ) {

        User user = userService.findUserByEmail(email);

        Cart cart = cartService.getCartEntity(user.getId());

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        FoodOrder order = new FoodOrder();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setDeliveryAddress(request.deliveryAddress());
        order.setCreatedAt(LocalDateTime.now());

        double total = 0.0;

        for (CartItem cartItem : cart.getItems()) {

            var managedFoodItem =
                    foodItemService.getFoodItemEntityForUpdate(
                            cartItem.getFoodItem().getId()
                    );

            if (
                    cartItem.getQuantity()
                            > managedFoodItem.getStockQuantity()
            ) {

                throw new BadRequestException(
                        "Insufficient stock for "
                                + managedFoodItem.getName()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setFoodItem(managedFoodItem);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(managedFoodItem.getPrice());

            order.getItems().add(orderItem);

            total += orderItem.getUnitPrice()
                    * orderItem.getQuantity();

            managedFoodItem.setStockQuantity(
                    managedFoodItem.getStockQuantity()
                            - cartItem.getQuantity()
            );

            foodItemService.syncAvailability(managedFoodItem);
        }

        order.setTotalAmount(total);

        FoodOrder savedOrder =
                foodOrderRepository.save(order);

        Payment payment = new Payment();

        payment.setOrder(savedOrder);
        payment.setAmount(total);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        cartService.clearCart(cart);

        log.info(
                "Placed order {} for user {}",
                savedOrder.getId(),
                user.getEmail()
        );

        return mapOrder(savedOrder.getId(), savedOrder);
    }

    /**
     * Retrieves all orders belonging to the authenticated user.
     */
    public List<OrderResponse> getOrdersForCurrentUser(
            String email
    ) {

        User user = userService.findUserByEmail(email);

        return foodOrderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(order ->
                        mapOrder(order.getId(), order)
                )
                .toList();
    }

    /**
     * Retrieves all orders in the system.
     */
    public List<OrderResponse> getAllOrders() {

        return foodOrderRepository.findAll()
                .stream()
                .map(order ->
                        mapOrder(order.getId(), order)
                )
                .toList();
    }

    /**
     * Updates the status of an existing order.
     * Prevents invalid state transitions and restores stock
     * when orders are cancelled.
     */
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus status
    ) {

        FoodOrder order = getOrderEntity(orderId);

        if (
                order.getStatus() == OrderStatus.CANCELLED
                        && status != OrderStatus.CANCELLED
        ) {

            throw new BadRequestException(
                    "Cancelled orders cannot be reactivated"
            );
        }

        if (
                order.getStatus() == OrderStatus.DELIVERED
                        && status != OrderStatus.DELIVERED
        ) {

            throw new BadRequestException(
                    "Delivered orders cannot be changed"
            );
        }

        if (
                status == OrderStatus.CANCELLED
                        && order.getStatus()
                        != OrderStatus.CANCELLED
        ) {

            restoreStock(order);

            markPaymentAsFailed(order.getId());
        }

        order.setStatus(status);

        foodOrderRepository.save(order);

        log.info(
                "Updated order {} to {}",
                orderId,
                status
        );

        return mapOrder(orderId, order);
    }

    /**
     * Cancels an order belonging to the authenticated user.
     * Restores inventory quantities and marks payment as failed.
     */
    @Transactional
    public OrderResponse cancelOwnOrder(
            String email,
            Long orderId
    ) {

        User user = userService.findUserByEmail(email);

        FoodOrder order = getOrderEntity(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(
                    "Order is already cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException(
                    "Delivered orders cannot be cancelled"
            );
        }

        restoreStock(order);

        markPaymentAsFailed(orderId);

        order.setStatus(OrderStatus.CANCELLED);

        foodOrderRepository.save(order);

        log.info("Cancelled order {}", orderId);

        return mapOrder(orderId, order);
    }

    /**
     * Retrieves an order entity using its identifier.
     */
    public FoodOrder getOrderEntity(Long orderId) {

        return foodOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found"
                        )
                );
    }

    /**
     * Converts order entity data into API response format.
     * Includes payment information and ordered item details.
     */
    public OrderResponse mapOrder(
            Long orderId,
            FoodOrder order
    ) {

        Payment payment =
                paymentRepository.findByOrderId(orderId)
                        .orElse(null);

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getFoodItem().getId(),
                        item.getFoodItem().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice()
                                * item.getQuantity()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items,
                payment == null
                        ? null
                        : payment.getStatus()
        );
    }

    /**
     * Restores inventory quantities for all items
     * belonging to the cancelled order.
     */
    private void restoreStock(FoodOrder order) {

        for (OrderItem item : order.getItems()) {

            item.getFoodItem().setStockQuantity(
                    item.getFoodItem().getStockQuantity()
                            + item.getQuantity()
            );

            foodItemService.syncAvailability(
                    item.getFoodItem()
            );
        }
    }

    /**
     * Marks the payment associated with the order as failed.
     */
    private void markPaymentAsFailed(Long orderId) {

        paymentRepository.findByOrderId(orderId)
                .ifPresent(payment -> {

                    payment.setStatus(PaymentStatus.FAILED);

                    payment.setPaidAt(LocalDateTime.now());

                    paymentRepository.save(payment);
                });
    }
}
package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.OrderResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.OrderStatusUpdateRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.order.PlaceOrderRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.service.OrderService;
import lombok.RequiredArgsConstructor;

/**
 * This controller provides endpoints for:
 *     Placing customer orders
 *     Viewing customer order history
 *     Cancelling customer orders
 *     Viewing all system orders (Admin only)
 *     Updating order statuses (Admin only)

 * Customer operations are restricted to users with the CUSTOMER role, while administrative operations require the ADMIN role.
 */

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    /**
     * Service layer responsible for order-related business logic.
     */
    private final OrderService orderService;

    /**
     * Places a new order for the authenticated customer.
     * The order is created using the authenticated user's shopping cart and provided delivery/order details.
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(
            Principal principal,
            @Valid @RequestBody PlaceOrderRequest request
    ) {

        return orderService.placeOrder(
                principal.getName(),
                request
        );
    }

    /**
     * Retrieves all orders belonging to the authenticated customer.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderResponse> getOwnOrders(Principal principal) {

        return orderService.getOrdersForCurrentUser(
                principal.getName()
        );
    }

    /**
     * Cancels an existing order belonging to the authenticated customer.
     * Customers can only cancel their own orders.
     */
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponse cancelOwnOrder(
            Principal principal,
            @PathVariable Long orderId
    ) {

        return orderService.cancelOwnOrder(
                principal.getName(),
                orderId
        );
    }

    /**
     * Retrieves all orders in the system.
     * Access restricted to ADMIN users only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();
    }

    /**
     * Updates the status of an existing order.
     * Access restricted to ADMIN users only.
     */
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {

        return orderService.updateOrderStatus(
                orderId,
                request.status()
        );
    }
}
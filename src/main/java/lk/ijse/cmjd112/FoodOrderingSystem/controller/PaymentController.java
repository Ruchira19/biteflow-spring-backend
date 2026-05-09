package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.payment.PaymentRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.payment.PaymentResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.service.PaymentService;
import lombok.RequiredArgsConstructor;

/**

 * This controller provides endpoints for:

 *     Updating payment information
 *     Retrieving payment details for orders
 *     Viewing all payment records (Admin only)

 * Customers can manage payments related to their own orders, while administrators can access all payment records.
 */

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    /**
     * Service layer responsible for payment-related business logic.
     */
    private final PaymentService paymentService;

    /**
     * Updates payment information for an authenticated customer's order.
     * This endpoint allows customers to submit or update
     * payment-related details for their orders.
     */
    @PatchMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentResponse updatePayment(
            Principal principal,
            @Valid @RequestBody PaymentRequest request
    ) {

        return paymentService.updatePayment(
                principal.getName(),
                request
        );
    }

    /**
     * Retrieves payment details for a specific order.
     * Customers can access payment details for their own orders, while administrators can access payment details for any order.
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public PaymentResponse getPaymentForOrder(
            Principal principal,
            @PathVariable Long orderId
    ) {

        return paymentService.getPaymentForOrder(
                principal.getName(),
                orderId
        );
    }

    /**
     * Retrieves all payment records in the system. Access restricted to ADMIN users only.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponse> getAllPayments() {

        return paymentService.getAllPayments();
    }
}
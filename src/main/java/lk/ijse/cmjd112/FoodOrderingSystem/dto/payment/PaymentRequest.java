package lk.ijse.cmjd112.FoodOrderingSystem.dto.payment;

import lk.ijse.cmjd112.FoodOrderingSystem.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for payment update request.
 * Contains order ID, payment status, and transaction reference.
 */
public record PaymentRequest(
        @NotNull Long orderId,
        @NotNull PaymentStatus status,
        @NotBlank String transactionReference
) {
}

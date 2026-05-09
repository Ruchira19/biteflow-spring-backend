package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.payment.PaymentRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.payment.PaymentResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodOrder;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.OrderStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Payment;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Role;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for payment management operations.
 * Handles payment updates, payment retrieval,
 * payment validation, and payment response mapping.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    /**
     * Repository for payment database operations.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Service responsible for order-related operations.
     */
    private final OrderService orderService;

    /**
     * Service responsible for user-related operations.
     */
    private final UserService userService;

    /**
     * Updates payment information for an existing order.
     * Validates ownership and prevents payments
     * for cancelled orders.
     */
    @Transactional
    public PaymentResponse updatePayment(
            String email,
            PaymentRequest request
    ) {

        userService.findUserByEmail(email);

        FoodOrder order =
                orderService.getOrderEntity(request.orderId());

        Payment payment = paymentRepository
                .findByOrderId(order.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        if (!order.getUser().getEmail().equals(email)) {

            throw new BadRequestException(
                    "You can only pay for your own orders"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new BadRequestException(
                    "Cancelled orders cannot be paid"
            );
        }

        payment.setStatus(request.status());

        payment.setPaidAt(LocalDateTime.now());

        payment.setTransactionReference(
                request.transactionReference()
        );

        paymentRepository.save(payment);

        log.info(
                "Updated payment {} for order {}",
                payment.getId(),
                order.getId()
        );

        return mapPayment(payment);
    }

    /**
     * Retrieves all payment records in the system.
     */
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapPayment)
                .toList();
    }

    /**
     * Retrieves payment details for a specific order.
     * Customers can only access their own payment records,
     * while administrators can access all payment records.
     */
    public PaymentResponse getPaymentForOrder(
            String email,
            Long orderId
    ) {

        var user = userService.findUserByEmail(email);

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found"
                        )
                );

        if (
                user.getRole() == Role.CUSTOMER
                        && !payment.getOrder()
                        .getUser()
                        .getId()
                        .equals(user.getId())
        ) {

            throw new ResourceNotFoundException(
                    "Payment not found"
            );
        }

        return mapPayment(payment);
    }

    /**
     * Converts payment entity data into API response format.
     */
    private PaymentResponse mapPayment(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getTransactionReference()
        );
    }
}
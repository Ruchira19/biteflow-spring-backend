package lk.ijse.cmjd112.FoodOrderingSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartItemRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartItemResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartItemUpdateRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.Cart;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.CartItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodItem;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.FoodStatus;
import lk.ijse.cmjd112.FoodOrderingSystem.entity.User;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.BadRequestException;
import lk.ijse.cmjd112.FoodOrderingSystem.exception.ResourceNotFoundException;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.CartItemRepository;
import lk.ijse.cmjd112.FoodOrderingSystem.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for customer shopping cart operations.
 * Handles cart retrieval, item management,
 * stock validation, and cart total calculations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    /**
     * Repository for cart database operations.
     */
    private final CartRepository cartRepository;

    /**
     * Repository for cart item database operations.
     */
    private final CartItemRepository cartItemRepository;

    /**
     * Service responsible for food item operations.
     */
    private final FoodItemService foodItemService;

    /**
     * Service responsible for user-related operations.
     */
    private final UserService userService;

    /**
     * Retrieves the shopping cart for the authenticated user.
     */
    public CartResponse getCartForUser(String email) {

        User user = userService.findUserByEmail(email);

        Cart cart = getCartEntity(user.getId());

        return mapCart(cart);
    }

    /**
     * Adds a food item to the authenticated user's cart.
     * Validates food availability and stock quantity before adding.
     */
    @Transactional
    public CartResponse addItem(
            String email,
            CartItemRequest request
    ) {

        User user = userService.findUserByEmail(email);

        Cart cart = getCartEntity(user.getId());

        FoodItem foodItem =
                foodItemService.getFoodItemEntity(request.foodItemId());

        if (foodItem.getStatus() != FoodStatus.AVAILABLE) {
            throw new BadRequestException("Food item is not available");
        }

        CartItem cartItem = cartItemRepository
                .findByCartIdAndFoodItemId(
                        cart.getId(),
                        foodItem.getId()
                )
                .orElseGet(() -> {

                    CartItem item = new CartItem();

                    item.setCart(cart);
                    item.setFoodItem(foodItem);
                    item.setQuantity(0);

                    cart.getItems().add(item);

                    return item;
                });

        int nextQuantity =
                cartItem.getQuantity() + request.quantity();

        if (nextQuantity > foodItem.getStockQuantity()) {
            throw new BadRequestException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(nextQuantity);

        cartItemRepository.save(cartItem);

        log.info(
                "Added food item {} to cart {}",
                foodItem.getName(),
                cart.getId()
        );

        return mapCart(cart);
    }

    /**
     * Updates the quantity of an existing cart item.
     * Validates stock availability before updating.
     */
    @Transactional
    public CartResponse updateItem(
            String email,
            Long cartItemId,
            CartItemUpdateRequest request
    ) {

        User user = userService.findUserByEmail(email);

        Cart cart = getCartEntity(user.getId());

        CartItem cartItem =
                getOwnedCartItem(cart, cartItemId);

        if (
                request.quantity()
                        > cartItem.getFoodItem().getStockQuantity()
        ) {

            throw new BadRequestException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(request.quantity());

        cartItemRepository.save(cartItem);

        log.info("Updated cart item {}", cartItemId);

        return mapCart(cart);
    }

    /**
     * Removes an item from the authenticated user's cart.
     */
    @Transactional
    public CartResponse removeItem(
            String email,
            Long cartItemId
    ) {

        User user = userService.findUserByEmail(email);

        Cart cart = getCartEntity(user.getId());

        CartItem cartItem =
                getOwnedCartItem(cart, cartItemId);

        cart.getItems().remove(cartItem);

        log.info("Removed cart item {}", cartItemId);

        return mapCart(cart);
    }

    /**
     * Removes all items from the authenticated user's cart.
     */
    @Transactional
    public CartResponse clearCart(String email) {

        User user = userService.findUserByEmail(email);

        Cart cart = getCartEntity(user.getId());

        cart.getItems().clear();

        log.info("Cleared cart {}", cart.getId());

        return mapCart(cart);
    }

    /**
     * Removes all items from the provided cart entity.
     */
    @Transactional
    public void clearCart(Cart cart) {

        cart.getItems().clear();
    }

    /**
     * Retrieves the cart entity associated with the given user.
     */
    public Cart getCartEntity(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found")
                );
    }

    /**
     * Retrieves a cart item belonging to the specified cart.
     */
    private CartItem getOwnedCartItem(
            Cart cart,
            Long cartItemId
    ) {

        return cart.getItems().stream()
                .filter(item ->
                        item.getId().equals(cartItemId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found"
                        )
                );
    }

    /**
     * Converts cart entity data into API response format.
     * Calculates item totals and overall cart total.
     */
    public CartResponse mapCart(Cart cart) {

        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getFoodItem().getId(),
                        item.getFoodItem().getName(),
                        item.getFoodItem().getPrice(),
                        item.getQuantity(),
                        item.getFoodItem().getPrice()
                                * item.getQuantity(),
                        item.getFoodItem().getStockQuantity()
                ))
                .toList();

        double total = items.stream()
                .mapToDouble(CartItemResponse::lineTotal)
                .sum();

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                items,
                total
        );
    }
}
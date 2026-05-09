package lk.ijse.cmjd112.FoodOrderingSystem.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartItemRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartItemUpdateRequest;
import lk.ijse.cmjd112.FoodOrderingSystem.dto.cart.CartResponse;
import lk.ijse.cmjd112.FoodOrderingSystem.service.CartService;
import lombok.RequiredArgsConstructor;

/**
 
 * This controller allows authenticated customers to:
 *     View their shopping cart
 *     Add items to the cart
 *     Update cart item quantities
 *     Remove items from the cart
 *     Clear the entire cart
 * All endpoints in this controller are restricted to users with the CUSTOMER role.
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    /**
     * Service layer responsible for cart-related business logic.
     */
    private final CartService cartService;

    /**
     * Retrieves the authenticated customer's shopping cart.
     */
    @GetMapping
    public CartResponse getCart(Principal principal) {

        return cartService.getCartForUser(principal.getName());
    }

    /**
     * Adds a new item to the authenticated customer's cart.
     */
    @PostMapping("/items")
    public CartResponse addItem(
            Principal principal,
            @Valid @RequestBody CartItemRequest request
    ) {

        return cartService.addItem(principal.getName(), request);
    }

    /**
     * Updates an existing cart item's quantity.
     */
    @PutMapping("/items/{cartItemId}")
    public CartResponse updateItem(
            Principal principal,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {

        return cartService.updateItem(
                principal.getName(),
                cartItemId,
                request
        );
    }

    /**
     * Removes an item from the authenticated customer's cart.
     */
    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(
            Principal principal,
            @PathVariable Long cartItemId
    ) {

        return cartService.removeItem(
                principal.getName(),
                cartItemId
        );
    }

    /**
     * Clears all items from the authenticated customer's cart.
     */
    @DeleteMapping
    public CartResponse clearCart(Principal principal) {

        return cartService.clearCart(principal.getName());
    }
}
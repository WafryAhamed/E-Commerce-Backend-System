package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.cart.AddCartItemRequest;
import com.ecommerce.backend.dto.cart.CartItemResponse;
import com.ecommerce.backend.dto.cart.CartResponse;
import com.ecommerce.backend.dto.cart.UpdateCartItemRequest;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final CurrentUserService currentUserService;

    public CartResponse getMyCart() {
        User currentUser = currentUserService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);
        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);

        Product product = productService.getEntityById(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(CartItem.builder().cart(cart).product(product).quantity(0).build());

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Item added to cart. user={}, product={}, quantity={}", currentUser.getUsername(), product.getName(), request.getQuantity());
        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long cartItemId, UpdateCartItemRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to current user");
        }

        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + item.getProduct().getName());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        log.info("Cart item updated. user={}, itemId={}, quantity={}", currentUser.getUsername(), cartItemId, request.getQuantity());

        return mapCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long cartItemId) {
        User currentUser = currentUserService.getCurrentUser();
        Cart cart = getOrCreateCart(currentUser);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to current user");
        }

        cartItemRepository.delete(item);
        log.info("Cart item removed. user={}, itemId={}", currentUser.getUsername(), cartItemId);

        return mapCartResponse(cart);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cartItemRepository.deleteByCart(cart);
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    public List<CartItem> getItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    private CartResponse mapCartResponse(Cart cart) {
        List<CartItemResponse> items = getItems(cart).stream().map(item -> {
            BigDecimal lineTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .unitPrice(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .lineTotal(lineTotal)
                    .build();
        }).toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalPrice(total)
                .build();
    }
}


package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.cart.AddCartItemRequest;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartItemRepository;
import com.ecommerce.backend.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItemShouldRejectWhenCombinedQuantityExceedsStock() {
        User user = User.builder().id(10L).username("customer").role(Role.ROLE_CUSTOMER).build();
        Cart cart = Cart.builder().id(20L).user(user).build();
        Category category = Category.builder().id(30L).name("Electronics").build();
        Product product = Product.builder()
                .id(40L)
                .name("Headphones")
                .stock(5)
                .price(new BigDecimal("99.99"))
                .category(category)
                .build();
        CartItem existingItem = CartItem.builder().id(50L).cart(cart).product(product).quantity(4).build();

        AddCartItemRequest request = new AddCartItemRequest();
        request.setProductId(40L);
        request.setQuantity(2);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productService.getEntityById(40L)).thenReturn(product);
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));

        assertThrows(BadRequestException.class, () -> cartService.addItem(request));
    }
}


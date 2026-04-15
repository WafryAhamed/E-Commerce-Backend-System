package com.ecommerce.backend.config;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.Category;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@shop.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ROLE_ADMIN)
                        .build());

                User customer = userRepository.save(User.builder()
                        .username("customer")
                        .email("customer@shop.com")
                        .password(passwordEncoder.encode("Customer@123"))
                        .role(Role.ROLE_CUSTOMER)
                        .build());

                cartRepository.save(Cart.builder().user(admin).build());
                cartRepository.save(Cart.builder().user(customer).build());
                log.info("Sample users created");
            }

            if (categoryRepository.count() == 0) {
                Category electronics = categoryRepository.save(Category.builder().name("Electronics").build());
                Category books = categoryRepository.save(Category.builder().name("Books").build());
                Category fashion = categoryRepository.save(Category.builder().name("Fashion").build());

                productRepository.save(Product.builder()
                        .name("Wireless Headphones")
                        .description("Noise-cancelling over-ear headphones")
                        .price(new BigDecimal("149.99"))
                        .stock(100)
                        .category(electronics)
                        .build());

                productRepository.save(Product.builder()
                        .name("Spring Boot in Action")
                        .description("Practical guide for building modern Java APIs")
                        .price(new BigDecimal("39.99"))
                        .stock(80)
                        .category(books)
                        .build());

                productRepository.save(Product.builder()
                        .name("Classic T-Shirt")
                        .description("Comfortable cotton t-shirt")
                        .price(new BigDecimal("19.99"))
                        .stock(200)
                        .category(fashion)
                        .build());
                log.info("Sample categories and products created");
            }
        };
    }
}


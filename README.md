# E-Commerce Backend System (Spring Boot + PostgreSQL)

Production-ready backend API for an e-commerce application, built with Java, Spring Boot, Spring Security (JWT), JPA, and PostgreSQL.

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL
- JWT (`jjwt`)
- Lombok
- Maven

## Features Implemented

- JWT-based authentication (`register`, `login`)
- Role-based authorization (`ROLE_ADMIN`, `ROLE_CUSTOMER`)
- Product management (Admin create/update/delete, public read)
- Category management (Admin create/update/delete, public read)
- Cart management (add/update/remove/get current cart)
- Order management (place order, view own history, admin status update)
- DTO-based API contracts and validation
- Global exception handling with structured error JSON
- Sample startup data via `DataInitializer`

## Project Structure

```text
src/main/java/com/ecommerce/backend
|- config
|  |- DataInitializer.java
|  |- SecurityConfig.java
|- controller
|  |- AuthController.java
|  |- CategoryController.java
|  |- ProductController.java
|  |- CartController.java
|  |- OrderController.java
|- dto
|  |- auth
|  |- category
|  |- product
|  |- cart
|  |- order
|  \- common
|- exception
|  |- GlobalExceptionHandler.java
|  |- ErrorResponse.java
|  |- BadRequestException.java
|  \- ResourceNotFoundException.java
|- model
|  |- User.java
|  |- Category.java
|  |- Product.java
|  |- Cart.java
|  |- CartItem.java
|  |- Order.java
|  |- OrderItem.java
|  |- Role.java
|  \- OrderStatus.java
|- repository
|- security
|  |- JwtService.java
|  |- JwtAuthenticationFilter.java
|  \- CustomUserDetailsService.java
|- service
\- ECommerceBackendSystemApplication.java

src/main/resources
\- application.properties

database
|- schema.sql
\- sample-data.sql
```

## Database Schema (Normalized)

Implemented tables:

- `users (id, username, email, password, role, created_at)`
- `products (id, name, description, price, stock, category_id, created_at)`
- `categories (id, name)`
- `orders (id, user_id, total_price, status, created_at)`
- `order_items (id, order_id, product_id, quantity, price)`
- `cart (id, user_id)`
- `cart_items (id, cart_id, product_id, quantity)`

Reference SQL is available in `database/schema.sql`.

## Configuration

Update `src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `app.jwt.secret`
- `app.jwt.expiration-ms`

## Default Seed Data

Created at startup by `DataInitializer`:

- Admin user: `admin / Admin@123`
- Customer user: `customer / Customer@123`
- Categories: Electronics, Books, Fashion
- Sample products for each category

## API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Categories

- `GET /api/categories` (public)
- `POST /api/categories` (admin)
- `PUT /api/categories/{id}` (admin)
- `DELETE /api/categories/{id}` (admin)

### Products

- `GET /api/products` (public)
- `GET /api/products/{id}` (public)
- `POST /api/products` (admin)
- `PUT /api/products/{id}` (admin)
- `DELETE /api/products/{id}` (admin)

### Cart

- `GET /api/cart` (customer/admin)
- `POST /api/cart/items` (customer/admin)
- `PUT /api/cart/items/{cartItemId}` (customer/admin)
- `DELETE /api/cart/items/{cartItemId}` (customer/admin)

### Orders

- `POST /api/orders/place` (customer/admin)
- `GET /api/orders/my/history` (customer/admin)
- `PUT /api/orders/{orderId}/status` (admin)

## Build and Run

```powershell
Set-Location "E:\Inventory Management System\E-Commerce-Backend-System"
mvn clean package
mvn spring-boot:run
```

## Quick Test Commands

```powershell
# Register
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/register" -ContentType "application/json" -Body '{"username":"john","email":"john@example.com","password":"Password@123"}'

# Login
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body '{"username":"admin","password":"Admin@123"}'
$token = $login.token

# Create category (admin)
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/categories" -Headers @{ Authorization = "Bearer $token" } -ContentType "application/json" -Body '{"name":"Home"}'
```

## Security Notes

- Passwords are hashed using BCrypt.
- JWT is required for protected endpoints via `Authorization: Bearer <token>`.
- Role checks are enforced in `SecurityConfig`.

## Validation and Error Handling

- Request validation is implemented with Jakarta Validation annotations.
- Centralized exception handling is in `GlobalExceptionHandler`.
- Errors return structured JSON (`timestamp`, `status`, `message`, `path`, validation details).


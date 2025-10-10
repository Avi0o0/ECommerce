# Cart Service

A microservice component of the E-Commerce platform responsible for managing user shopping carts and cart items.

## Overview

The Cart Service provides comprehensive shopping cart functionality including:
- Adding products to cart
- Updating product quantities
- Removing products from cart
- Clearing entire cart
- Retrieving cart contents with product details
- Integration with Product Service for real-time product information

## Architecture

### Layered Architecture
- **Controller Layer**: REST API endpoints with validation
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access with JPA repositories
- **Entity Layer**: JPA entities with proper relationships

### Key Components

#### Entities
- **Cart**: User's shopping cart (id, user_id, created_at, updated_at)
- **CartItem**: Individual items in cart (id, cart_id, product_id, quantity, price_at_addition)

#### DTOs
- **AddToCartRequest**: Input validation for adding products
- **UpdateQuantityRequest**: Input validation for quantity updates
- **CartResponse**: Complete cart information with items
- **CartItemResponse**: Individual cart item with product details

#### Services
- **CartService**: Core business logic with CRUD operations
- **ProductServiceClient**: Feign client for Product Service integration

## Database Schema

### PostgreSQL Tables

#### Carts Table
```sql
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### Cart Items Table
```sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price_at_addition DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
);
```

## API Endpoints

### Cart Management
- `GET /cart?userId={userId}` - Get user's cart with all items
- `POST /cart/add?userId={userId}` - Add product to cart
- `PUT /cart/update?userId={userId}` - Update product quantity
- `DELETE /cart/remove/{productId}?userId={userId}` - Remove product from cart
- `DELETE /cart/clear?userId={userId}` - Clear entire cart

### Cart Information
- `GET /cart/count?userId={userId}` - Get total number of items in cart
- `GET /cart/total?userId={userId}` - Get total price of cart

## Request/Response Examples

### Add Product to Cart
```http
POST /cart/add?userId=123
Content-Type: application/json

{
    "productId": 456,
    "quantity": 2
}
```

### Cart Response
```json
{
    "id": 1,
    "userId": 123,
    "items": [
        {
            "id": 1,
            "productId": 456,
            "productName": "Laptop",
            "productDescription": "High-performance laptop",
            "productImageUrl": "https://example.com/laptop.jpg",
            "quantity": 2,
            "priceAtAddition": 999.99,
            "currentPrice": 999.99,
            "totalPrice": 1999.98,
            "addedAt": "2024-01-15T10:30:00"
        }
    ],
    "totalItems": 2,
    "totalPrice": 1999.98,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
}
```

## Service Integration

### Product Service Integration
- **Real-time Product Validation**: Validates product existence and availability
- **Price Information**: Fetches current product prices
- **Product Details**: Retrieves product name, description, and image URL
- **Graceful Degradation**: Handles Product Service unavailability

### API Gateway Integration
- **Authentication**: Requests are validated via API Gateway
- **User Context**: User information passed through gateway headers
- **No Direct Authentication**: Service relies on gateway for security

## Business Logic

### Cart Operations
1. **Add to Cart**: 
   - Validates product exists and is active
   - Creates cart if user doesn't have one
   - Adds new item or updates existing quantity
   - Stores price at time of addition

2. **Update Quantity**:
   - Validates cart and item exist
   - Updates quantity for specific product
   - Maintains price at addition

3. **Remove from Cart**:
   - Validates cart and item exist
   - Removes specific product from cart
   - Updates cart totals

4. **Clear Cart**:
   - Removes all items from user's cart
   - Used after successful order placement

### Data Consistency
- **Price Preservation**: Stores price at time of addition
- **Quantity Validation**: Ensures minimum quantity of 1
- **Cart Uniqueness**: One cart per user
- **Cascade Operations**: Cart deletion removes all items

## Error Handling

### Custom Exceptions
- `CartNotFoundException`: When cart doesn't exist for user
- `CartItemNotFoundException`: When product not found in cart
- `ProductNotFoundException`: When product doesn't exist or is inactive

### Error Response Format
```json
{
    "status": 404,
    "error": "Cart Not Found",
    "message": "Cart not found for user: 123",
    "timestamp": "2024-01-15T10:30:00",
    "path": "/cart"
}
```

## Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=cart-service
server.port=8086

# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom-db
spring.datasource.username=postgres
spring.datasource.password=system123

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Eureka Service Discovery
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## Monitoring and Observability

### Actuator Endpoints
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Service metrics

### Logging
- **Request Logging**: All incoming requests
- **Business Operations**: Cart operations and product integrations
- **Error Logging**: Failed operations and exceptions
- **Performance Logging**: Database operations and external calls

## Testing

### Test Configuration
- H2 in-memory database for testing
- Mock Product Service client
- Context loading tests

### Manual Testing
```bash
# Get user's cart
curl "http://localhost:8086/cart?userId=123"

# Add product to cart
curl -X POST "http://localhost:8086/cart/add?userId=123" \
     -H "Content-Type: application/json" \
     -d '{"productId": 456, "quantity": 2}'

# Update quantity
curl -X PUT "http://localhost:8086/cart/update?userId=123" \
     -H "Content-Type: application/json" \
     -d '{"productId": 456, "quantity": 3}'

# Remove product
curl -X DELETE "http://localhost:8086/cart/remove/456?userId=123"

# Clear cart
curl -X DELETE "http://localhost:8086/cart/clear?userId=123"
```

## Deployment

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server running
- Product Service running and accessible

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/cart-service-0.0.1-SNAPSHOT.jar
```

### Docker Support
The service can be containerized and deployed in a microservices environment.

## Best Practices Implemented

1. **Clean Architecture**: Clear separation of concerns across layers
2. **Data Integrity**: Proper foreign key relationships and constraints
3. **Price Preservation**: Stores price at time of addition for consistency
4. **Graceful Degradation**: Handles external service failures
5. **Comprehensive Validation**: Input validation with meaningful error messages
6. **Transaction Management**: Proper transaction handling for data consistency
7. **Error Handling**: Centralized exception handling with proper HTTP status codes
8. **Logging**: Structured logging for monitoring and debugging
9. **Service Integration**: Clean integration with Product Service via Feign
10. **Database Design**: Normalized schema with proper relationships

## Security Considerations

- **No Direct Authentication**: Relies on API Gateway for security
- **User Isolation**: Each user can only access their own cart
- **Input Validation**: Comprehensive validation of all inputs
- **SQL Injection Prevention**: Uses JPA repositories with parameterized queries
- **Data Privacy**: No sensitive user data stored beyond cart information

## Performance Considerations

- **Lazy Loading**: Cart items loaded lazily to improve performance
- **Database Indexing**: Proper indexing on user_id and product_id
- **Connection Pooling**: Database connection pooling for better performance
- **Caching**: Can be extended with Redis caching for frequently accessed carts
- **Batch Operations**: Efficient batch operations for cart clearing

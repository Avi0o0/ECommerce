# Order Service Implementation

## Overview
The Order Service is a microservice responsible for managing orders in the e-commerce platform. It handles order creation, payment processing integration, and order retrieval with proper JWT-based authentication and role-based authorization.

## Architecture
- **Framework**: Spring Boot 3.5.6 with Spring Cloud
- **Database**: PostgreSQL with JPA/Hibernate
- **Service Discovery**: Eureka Client
- **Inter-service Communication**: Feign Clients
- **Authentication**: JWT token validation via User Service
- **API Documentation**: OpenAPI/Swagger

## Key Features

### 1. Order Management
- Create orders with PENDING status
- Process payments via Payment Service integration
- Update order status based on payment response (COMPLETED/FAILED)
- Retrieve orders by ID or user ID

### 2. Payment Integration
- Feign Client integration with Payment Service
- Automatic payment processing during checkout
- Order status updates based on payment results

### 3. Security
- JWT token validation for all endpoints
- Role-based access control:
  - **USER**: Can perform checkout and view their orders
  - **ADMIN**: Can view all orders
- Token validation via User Service

### 4. Error Handling
- Global exception handling with standardized error responses
- Comprehensive logging for debugging and monitoring
- Graceful error handling for payment failures

## API Endpoints

### 1. POST /orders/checkout
**Access**: USER only
**Description**: Create a new order and process payment

**Request Body**:
```json
{
  "userId": 1,
  "totalAmount": 99.99,
  "paymentMethod": "CREDIT_CARD",
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 49.99
    }
  ]
}
```

**Response**:
```json
{
  "id": 1,
  "userId": 1,
  "totalAmount": 99.99,
  "paymentId": 123,
  "status": "COMPLETED",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:05",
  "orderItems": [
    {
      "id": 1,
      "productId": 1,
      "quantity": 2,
      "price": 49.99
    }
  ]
}
```

### 2. GET /orders/{orderId}
**Access**: USER/ADMIN
**Description**: Get order details by ID

**Response**: Order details with items and payment information

### 3. GET /orders/user/{userId}
**Access**: USER/ADMIN
**Description**: Get all orders for a specific user

**Response**: Array of order details

## Database Schema

### Orders Table
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_id BIGINT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

### Order Items Table
```sql
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

## Order Status Flow
1. **PENDING**: Order created, awaiting payment
2. **COMPLETED**: Payment successful
3. **FAILED**: Payment failed or error occurred

## Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=order-service
server.port=8083
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom-db
spring.datasource.username=postgres
spring.datasource.password=system123
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.ecom.orderservice=INFO
```

## Dependencies

### Core Dependencies
- `spring-boot-starter-web`: REST API support
- `spring-boot-starter-data-jpa`: Database operations
- `spring-cloud-starter-netflix-eureka-client`: Service discovery
- `spring-cloud-starter-openfeign`: Inter-service communication
- `spring-boot-starter-validation`: Request validation
- `postgresql`: Database driver

### Development Dependencies
- `springdoc-openapi-starter-webmvc-ui`: API documentation
- `spring-boot-starter-actuator`: Health checks and metrics

## Testing

### Postman Collection
A Postman collection is provided (`postman-collection.json`) with:
- Checkout order endpoint
- Get order by ID endpoint
- Get orders by user ID endpoint

### Environment Variables
Set the following variables in Postman:
- `base_url`: http://localhost:8083
- `jwt_token`: Valid JWT token from User Service
- `order_id`: Order ID for testing
- `user_id`: User ID for testing

## Error Responses

All errors return a standardized format:
```json
{
  "status": "ERROR_CODE",
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00"
}
```

### Common Error Codes
- `UNAUTHORIZED`: Invalid or missing JWT token
- `ORDER_NOT_FOUND`: Order with specified ID not found
- `VALIDATION_FAILED`: Request validation errors
- `CHECKOUT_FAILED`: Payment processing failed
- `INTERNAL_SERVER_ERROR`: Unexpected server error

## Integration Points

### Payment Service
- **Endpoint**: `/payments/process`
- **Method**: POST
- **Purpose**: Process payment for orders
- **Response**: Payment status and transaction details

### User Service
- **Endpoint**: `/auth/validate`
- **Method**: POST
- **Purpose**: Validate JWT tokens and get user details
- **Response**: Token validation result with user roles

## Monitoring and Logging

### Logging Levels
- **INFO**: General service operations
- **ERROR**: Error conditions and exceptions
- **DEBUG**: Detailed request/response information

### Health Checks
- **Endpoint**: `/actuator/health`
- **Purpose**: Service health monitoring
- **Response**: Service status and dependencies

## Deployment

### Prerequisites
1. PostgreSQL database running on port 5432
2. Eureka Server running on port 8761
3. User Service running and accessible
4. Payment Service running and accessible

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

### Docker Support
The service can be containerized using the provided Dockerfile and docker-compose configuration.

## Security Considerations

1. **JWT Token Validation**: All endpoints require valid JWT tokens
2. **Role-Based Access**: Different endpoints have different access levels
3. **Input Validation**: All request data is validated
4. **Error Handling**: Sensitive information is not exposed in error messages
5. **Logging**: Security events are logged for audit purposes

## Future Enhancements

1. **Order Status Updates**: Add endpoints for updating order status
2. **Order Cancellation**: Implement order cancellation functionality
3. **Order History**: Add pagination and filtering for order history
4. **Notifications**: Integrate with notification service for order updates
5. **Analytics**: Add order analytics and reporting capabilities

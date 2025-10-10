# Order Service

A comprehensive microservice for managing orders in the E-Commerce platform. This service handles order creation, status tracking, payment processing, and order history management.

## 🏗️ Architecture Overview

The Order Service follows a clean layered architecture pattern:
- **Controller Layer**: REST endpoints for order operations
- **Service Layer**: Business logic for order management and inter-service communication
- **Repository Layer**: Data access using Spring Data JPA
- **Entity Layer**: JPA entities for database mapping

## 📊 Database Schema

### Orders Table
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Order Items Table
```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

## 🔄 Order Status Flow

```
PENDING → PAID → SHIPPED → DELIVERED
    ↓
CANCELED
```

- **PENDING**: Order created, awaiting payment
- **PAID**: Payment processed successfully
- **SHIPPED**: Order dispatched for delivery
- **DELIVERED**: Order completed
- **CANCELED**: Order cancelled (stock restored)

## 🚀 Features

### Core Functionality
- ✅ **Order Creation**: Place orders from cart or with specific items
- ✅ **Status Management**: Update order status with validation
- ✅ **Order History**: Retrieve user's order history with pagination
- ✅ **Order Cancellation**: Cancel orders and restore stock
- ✅ **Payment Processing**: Integrate with Payment Service
- ✅ **Stock Management**: Reserve and release stock via Product Service

### Advanced Features
- ✅ **Transaction Management**: ACID compliance for order operations
- ✅ **Inter-Service Communication**: Feign clients for service integration
- ✅ **Comprehensive Logging**: Detailed logging for monitoring
- ✅ **Exception Handling**: Custom exceptions with proper error responses
- ✅ **API Documentation**: OpenAPI/Swagger integration
- ✅ **Health Monitoring**: Actuator endpoints for observability

## 🔗 Service Integrations

### Product Service
- **Stock Validation**: Check product availability
- **Stock Reservation**: Reserve stock during order creation
- **Stock Release**: Release stock on order cancellation
- **Product Details**: Fetch product information for order items

### Cart Service
- **Cart Retrieval**: Get user's cart items
- **Cart Clearing**: Clear cart after successful order
- **Cart Validation**: Ensure cart is not empty

### Payment Service
- **Payment Processing**: Process payments for orders
- **Payment Status**: Track payment status
- **Refund Processing**: Handle payment refunds

## 📡 API Endpoints

### Order Management
```http
POST   /orders                    # Place new order
POST   /orders/from-cart          # Place order from cart
GET    /orders/{orderId}          # Get order by ID
GET    /orders/{orderId}/user/{userId}  # Get order by ID and user ID
PUT    /orders/{orderId}/status   # Update order status
PUT    /orders/{orderId}/cancel   # Cancel order
POST   /orders/{orderId}/payment  # Process payment
```

### Order History
```http
GET    /orders/user/{userId}           # Get user's order history
GET    /orders/user/{userId}/paged     # Get paginated order history
GET    /orders/status/{status}         # Get orders by status
```

### Example Requests

#### Place Order from Cart
```bash
curl -X POST "http://localhost:8083/orders/from-cart?userId=123"
```

#### Place Order with Specific Items
```bash
curl -X POST "http://localhost:8083/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

#### Update Order Status
```bash
curl -X PUT "http://localhost:8083/orders/1/status" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PAID"
  }'
```

#### Get Order History
```bash
curl -X GET "http://localhost:8083/orders/user/123"
```

## 🛠️ Technology Stack

- **Java 17**: Modern Java features and performance
- **Spring Boot 3.5.6**: Rapid application development
- **Spring Cloud**: Microservices architecture support
- **Spring Data JPA**: Data persistence layer
- **PostgreSQL**: Primary database
- **H2**: In-memory database for testing
- **OpenFeign**: Inter-service communication
- **OpenAPI/Swagger**: API documentation
- **Actuator**: Application monitoring

## 📋 Dependencies

### Core Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Database Dependencies
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## ⚙️ Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=order-service
server.port=8083

# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom-db
spring.datasource.username=postgres
spring.datasource.password=system123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Eureka Service Discovery
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Logging Configuration
logging.level.com.ecom.orderservice=INFO
logging.level.org.springframework.web=DEBUG

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

## 🔧 Business Logic

### Order Creation Process
1. **Validate Cart/Items**: Ensure cart is not empty or items are valid
2. **Check Stock**: Verify product availability via Product Service
3. **Reserve Stock**: Reserve stock to prevent overselling
4. **Create Order**: Save order with PENDING status
5. **Create Order Items**: Save order items with product details
6. **Clear Cart**: Remove items from user's cart
7. **Return Order**: Return created order details

### Order Status Updates
1. **Validate Status**: Check if status transition is allowed
2. **Update Order**: Save new status with timestamp
3. **Handle Side Effects**: Process status-specific actions (e.g., stock release for cancellation)

### Payment Processing
1. **Validate Order**: Ensure order is in PENDING status
2. **Process Payment**: Call Payment Service
3. **Update Status**: Change order status to PAID on successful payment
4. **Handle Failures**: Maintain PENDING status on payment failure

## 🚨 Exception Handling

### Custom Exceptions
- **OrderNotFoundException**: Order not found
- **InsufficientStockException**: Insufficient stock for order
- **OrderProcessingException**: General order processing errors
- **InvalidOrderStatusException**: Invalid status transitions

### Error Response Format
```json
{
  "status": 404,
  "error": "Order Not Found",
  "message": "Order not found with ID: 123",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/orders/123"
}
```

## 📊 Monitoring and Observability

### Actuator Endpoints
- `/actuator/health` - Service health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging
- **INFO Level**: Order operations and business events
- **DEBUG Level**: Detailed request/response logging
- **ERROR Level**: Exception details and error context

## 🧪 Testing

### Test Configuration
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
```

### Test Database
- **H2 In-Memory**: Fast test execution
- **Auto Schema Creation**: Automatic table creation
- **Eureka Disabled**: No service discovery in tests

## 🚀 Deployment

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server running
- Product Service running and accessible
- Cart Service running and accessible
- Payment Service running and accessible

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/order-service-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔐 Security Considerations

- **API Gateway Integration**: All requests routed through API Gateway
- **JWT Token Validation**: Handled by API Gateway
- **Role-Based Access**: User/Admin role validation
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: JPA parameterized queries

## 📈 Performance Considerations

- **Database Indexing**: Optimized queries with proper indexes
- **Lazy Loading**: Efficient data loading strategies
- **Connection Pooling**: Optimized database connections
- **Caching**: Potential for Redis integration
- **Async Processing**: Future enhancement for heavy operations

## 🔄 Future Enhancements

- **Order Analytics**: Order trend analysis and reporting
- **Inventory Integration**: Real-time stock updates
- **Notification Service**: Order status notifications
- **Order Tracking**: Real-time delivery tracking
- **Bulk Operations**: Batch order processing
- **Order Templates**: Recurring order functionality

## 📞 Support

For issues, questions, or contributions:
- **Email**: support@ecommerce.com
- **Documentation**: [API Documentation](http://localhost:8083/swagger-ui.html)
- **Health Check**: [Service Health](http://localhost:8083/actuator/health)

---

**Order Service** - Managing orders with precision and reliability in the E-Commerce ecosystem.

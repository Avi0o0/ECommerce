# Product Service

A microservice component of the E-Commerce platform responsible for managing product catalog operations.

## Overview

The Product Service provides comprehensive product management functionality including:
- Product CRUD operations
- Product filtering and search
- Category management
- Integration with Inventory and Notification services
- Role-based access control

## Architecture

### Layered Architecture
- **Controller Layer**: REST API endpoints with validation and documentation
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access with JPA repositories
- **Entity Layer**: JPA entities with proper relationships

### Key Components

#### Entities
- **Product**: Core product entity with fields (id, name, description, price, category_id, sku, image_url, is_active, created_at)
- **Category**: Product categorization with one-to-many relationship

#### DTOs
- **ProductRequest**: Input validation for product creation/updates
- **ProductResponse**: Structured response with category information
- **ProductFilterRequest**: Filtering criteria for product search

#### Services
- **ProductService**: Core business logic with CRUD operations
- **JwtService**: JWT token validation and processing
- **ProductServiceUserDetailsService**: User authentication service

#### Clients
- **InventoryServiceClient**: Feign client for inventory management
- **NotificationServiceClient**: Feign client for notification dispatch

## API Endpoints

### Public Endpoints (No Authentication Required)
- `GET /products` - Get all active products
- `GET /products/{id}` - Get product by ID
- `GET /products/filter` - Filter products by category, price range, or search text

### Admin Endpoints (JWT Token Required)
- `POST /products` - Create new product
- `PUT /products/{id}` - Update existing product
- `DELETE /products/{id}` - Soft delete product

### Query Parameters for Filtering
- `categoryId` - Filter by category ID
- `minPrice` - Minimum price filter
- `maxPrice` - Maximum price filter
- `searchText` - Search in product names

## Security

### JWT Authentication
- Stateless authentication using JWT tokens
- Role-based access control (ADMIN role required for CUD operations)
- Token validation with configurable expiration

### Security Configuration
- CSRF disabled for stateless API
- Session management set to STATELESS
- Method-level security enabled

## Database Schema

### Products Table
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    sku VARCHAR(50) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Categories Table
```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=product-service
server.port=8082

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom-db
spring.datasource.username=postgres
spring.datasource.password=system123

# JWT Configuration
security.jwt.secret=your-secret-key
security.jwt.expiration-seconds=1800

# Eureka Service Discovery
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## Integration

### Inventory Service Integration
- Automatic inventory record creation when products are added
- Inventory record deletion when products are removed
- Stock updates handled via Feign client

### Notification Service Integration
- Product creation notifications
- Product update notifications
- Product deletion notifications
- Admin notifications for catalog changes

## Error Handling

### Custom Exceptions
- `ProductNotFoundException`: When product is not found
- `CategoryNotFoundException`: When category is not found
- `DuplicateSkuException`: When SKU already exists

### Global Exception Handler
- Centralized error handling with `@ControllerAdvice`
- Structured error responses with HTTP status codes
- Validation error handling with field-specific messages

## Logging

### Log Levels
- `INFO`: General service operations
- `DEBUG`: Security and web request details
- Structured logging with timestamps and context

### Key Operations Logged
- Product CRUD operations
- Authentication attempts
- Service integration calls
- Error conditions

## Testing

### Test Configuration
- H2 in-memory database for testing
- Test-specific JWT configuration
- Context loading tests

### Test Structure
- Unit tests for service layer
- Integration tests for repository layer
- Controller tests with mock services

## API Documentation

### Swagger/OpenAPI
- Interactive API documentation available at `/swagger-ui.html`
- API documentation at `/api-docs`
- Comprehensive endpoint descriptions and examples

## Monitoring

### Actuator Endpoints
- Health checks at `/actuator/health`
- Application info at `/actuator/info`
- Metrics at `/actuator/metrics`

## Deployment

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server running
- Inventory Service and Notification Service available

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/product-service-0.0.1-SNAPSHOT.jar
```

### Docker Support
The service can be containerized and deployed in a microservices environment.

## Best Practices Implemented

1. **Clean Architecture**: Clear separation of concerns across layers
2. **Validation**: Comprehensive input validation with meaningful error messages
3. **Security**: JWT-based authentication with role-based authorization
4. **Error Handling**: Centralized exception handling with proper HTTP status codes
5. **Logging**: Structured logging for monitoring and debugging
6. **Documentation**: Comprehensive API documentation with Swagger
7. **Testing**: Unit and integration test coverage
8. **Integration**: Proper service-to-service communication via Feign clients
9. **Database Design**: Normalized schema with proper relationships
10. **Configuration**: Externalized configuration for different environments

# API Gateway

A Spring Cloud Gateway implementation that serves as the single entry point for all microservices in the E-Commerce platform, providing centralized authentication, authorization, and routing.

## Overview

The API Gateway acts as the front door for all client requests, handling:
- **JWT Token Validation**: Validates tokens with the User Service
- **Role-Based Authorization**: Enforces access control based on user roles
- **Request Routing**: Routes requests to appropriate microservices
- **Token Caching**: Caches validation results for performance
- **Error Handling**: Provides consistent error responses

## Architecture

### Core Components

#### Filters
- **JwtAuthenticationFilter**: Validates JWT tokens and extracts user information
- **RoleBasedAuthorizationFilter**: Enforces role-based access control
- **Global Filters**: Applied to all routes for consistent behavior

#### Services
- **JwtValidationService**: Calls User Service to validate tokens
- **Cache Management**: Redis-based token validation caching

#### Configuration
- **GatewayConfig**: Route definitions and WebClient configuration
- **SecurityConfig**: Spring Security configuration for reactive applications
- **CacheConfig**: Redis cache configuration

## API Endpoints

### Public Endpoints (No Authentication Required)
- `GET /auth/**` - Authentication endpoints (login, register, validate)
- `GET /actuator/**` - Health checks and monitoring
- `GET /swagger-ui/**` - API documentation
- `GET /gateway/health` - Gateway health check
- `GET /gateway/info` - Gateway information

### Protected Endpoints (Authentication Required)

#### Product Service (`/api/products/**`)
- **GET** `/api/products` - List products (USER, ADMIN)
- **GET** `/api/products/{id}` - Get product details (USER, ADMIN)
- **GET** `/api/products/filter` - Filter products (USER, ADMIN)
- **POST** `/api/products` - Create product (ADMIN only)
- **PUT** `/api/products/{id}` - Update product (ADMIN only)
- **DELETE** `/api/products/{id}` - Delete product (ADMIN only)

#### Order Service (`/api/orders/**`)
- **GET** `/api/orders` - List orders (USER, ADMIN)
- **POST** `/api/orders` - Create order (USER, ADMIN)
- **PUT** `/api/orders/{id}` - Update order (USER, ADMIN)

#### Payment Service (`/api/payments/**`)
- **GET** `/api/payments` - List payments (USER, ADMIN)
- **POST** `/api/payments` - Process payment (USER, ADMIN)

#### Cart Service (`/api/cart/**`)
- **GET** `/api/cart` - Get cart (USER, ADMIN)
- **POST** `/api/cart` - Add to cart (USER, ADMIN)
- **PUT** `/api/cart` - Update cart (USER, ADMIN)
- **DELETE** `/api/cart` - Clear cart (USER, ADMIN)

#### Inventory Service (`/api/inventory/**`)
- **GET** `/api/inventory` - List inventory (ADMIN only)
- **POST** `/api/inventory` - Add inventory (ADMIN only)
- **PUT** `/api/inventory/{id}` - Update inventory (ADMIN only)
- **DELETE** `/api/inventory/{id}` - Remove inventory (ADMIN only)

#### Notification Service (`/api/notifications/**`)
- **GET** `/api/notifications` - List notifications (ADMIN only)
- **POST** `/api/notifications` - Send notification (ADMIN only)
- **PUT** `/api/notifications/{id}` - Update notification (ADMIN only)
- **DELETE** `/api/notifications/{id}` - Delete notification (ADMIN only)

## Authentication Flow

### 1. Token Validation Process
```
Client Request → Gateway → JwtAuthenticationFilter → JwtValidationService → User Service
```

1. Client sends request with `Authorization: Bearer <token>` header
2. Gateway extracts token from Authorization header
3. Gateway calls User Service `/auth/validate` endpoint
4. User Service validates token and returns user info + roles
5. Gateway caches validation result (5 minutes TTL)
6. Gateway adds user info to request headers for downstream services

### 2. Authorization Process
```
Validated Request → RoleBasedAuthorizationFilter → Route Matching → Service Call
```

1. Gateway checks if user has required role for the endpoint
2. Role requirements are defined per endpoint pattern
3. If authorized, request is forwarded to target service
4. If unauthorized, returns 403 Forbidden

## Security Configuration

### JWT Token Validation
- **Validation Endpoint**: `http://user-service/auth/validate`
- **Cache TTL**: 5 minutes
- **Timeout**: 5 seconds
- **Error Handling**: Graceful fallback on service unavailability

### Role-Based Access Control
- **ADMIN Role**: Full access to all endpoints
- **USER Role**: Limited access to read operations and user-specific actions
- **Public Access**: Authentication endpoints and health checks

### Headers Added to Downstream Services
- `X-User-Token`: Original JWT token
- `X-Username`: Authenticated username
- `X-User-Roles`: Comma-separated list of user roles

## Routing Configuration

### Service Discovery
- **Eureka Integration**: Automatic service discovery
- **Load Balancing**: Round-robin load balancing
- **Health Checks**: Service health monitoring

### Route Patterns
- **API Routes**: `/api/{service}/**` → `lb://{SERVICE-NAME}`
- **Legacy Routes**: `/{service}/**` → `lb://{SERVICE-NAME}` (backward compatibility)
- **Strip Prefix**: Removes `/api` prefix before forwarding

### Service Mappings
- `USER-SERVICE` → Authentication and user management
- `PRODUCT-SERVICE` → Product catalog management
- `ORDER-SERVICE` → Order processing
- `PAYMENT-SERVICE` → Payment processing
- `CART-SERVICE` → Shopping cart management
- `INVENTORY-SERVICE` → Inventory management
- `NOTIFICATION-SERVICE` → Notification delivery

## Error Handling

### HTTP Status Codes
- **401 Unauthorized**: Invalid or missing token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Service not available
- **500 Internal Server Error**: Gateway or service errors

### Error Response Format
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid token",
  "timestamp": 1640995200000
}
```

## Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=apigateway
server.port=8080

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Gateway Configuration
spring.cloud.gateway.server.webflux.httpclient.connect-timeout=5000
spring.cloud.gateway.server.webflux.httpclient.response-timeout=10000

# Redis Cache Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis

# Logging Configuration
logging.level.com.ecom.apigateway=INFO
logging.level.org.springframework.cloud.gateway=DEBUG
```

## Monitoring and Observability

### Actuator Endpoints
- `/actuator/health` - Gateway health status
- `/actuator/info` - Gateway information
- `/actuator/gateway/routes` - Active routes
- `/actuator/gateway/filters` - Available filters

### Logging
- **Request Logging**: All incoming requests
- **Authentication Logging**: Token validation attempts
- **Authorization Logging**: Access control decisions
- **Error Logging**: Failed requests and errors

### Metrics
- Request count and response times
- Authentication success/failure rates
- Service availability metrics
- Cache hit/miss ratios

## Testing

### Test Endpoints
- `GET /gateway/health` - Verify gateway is running
- `GET /gateway/info` - Get gateway information
- `GET /gateway/test-auth` - Test authentication (requires valid token)

### Manual Testing
```bash
# Test public endpoint
curl http://localhost:8080/gateway/health

# Test protected endpoint (requires valid JWT token)
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:8080/gateway/test-auth

# Test product listing (requires USER or ADMIN role)
curl -H "Authorization: Bearer <your-jwt-token>" \
     http://localhost:8080/api/products
```

## Deployment

### Prerequisites
- Java 17+
- Redis server running
- Eureka Server running
- User Service running and accessible
- Target microservices running and registered with Eureka

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/apigateway-0.0.1-SNAPSHOT.jar
```

### Docker Support
The gateway can be containerized and deployed in a microservices environment.

## Best Practices Implemented

1. **Single Entry Point**: All requests go through the gateway
2. **Centralized Security**: Authentication and authorization handled centrally
3. **Token Caching**: Reduces validation calls to User Service
4. **Graceful Degradation**: Handles service unavailability gracefully
5. **Comprehensive Logging**: Detailed logging for monitoring and debugging
6. **Error Handling**: Consistent error responses across all services
7. **Service Discovery**: Automatic service discovery and load balancing
8. **Health Monitoring**: Built-in health checks and metrics
9. **Backward Compatibility**: Legacy route support
10. **Performance Optimization**: Reactive programming and caching

## Security Considerations

- **Token Expiration**: Tokens are validated for expiration
- **Role Validation**: Strict role-based access control
- **Service Isolation**: Downstream services don't handle authentication
- **Error Information**: Limited error details to prevent information leakage
- **CORS Configuration**: Configurable CORS for different environments
- **Rate Limiting**: Can be extended with rate limiting filters
- **SSL/TLS**: Should be configured for production environments

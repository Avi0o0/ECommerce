# Inventory Service

A comprehensive microservice for managing inventory stock in the E-Commerce platform. This service handles stock reservations, releases, deductions, and adjustments with optimistic locking to ensure data consistency.

## 🏗️ Architecture Overview

The Inventory Service follows a clean layered architecture pattern:
- **Controller Layer**: REST endpoints for inventory operations
- **Service Layer**: Business logic for stock management with transaction handling
- **Repository Layer**: Data access using Spring Data JPA with optimistic locking
- **Entity Layer**: JPA entity for database mapping with `@Version` for concurrency control

## 📊 Database Schema

### Inventory Table (PostgreSQL)
```sql
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    available_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0
);
```

**Key Features:**
- **Optimistic Locking**: `@Version` field prevents race conditions
- **Stock Tracking**: Separate `available_stock` and `reserved_stock` columns
- **Audit Trail**: `last_updated` timestamp for tracking changes
- **Unique Constraint**: One inventory record per product

## 🔄 Stock Management Flow

### Stock States
```
Available Stock → Reserved Stock → Deducted (Permanent)
     ↑                ↓
     ←── Released ←──
```

### Operations
1. **Reserve**: `available_stock` → `reserved_stock` (for cart/order)
2. **Release**: `reserved_stock` → `available_stock` (cart removed/order failed)
3. **Deduct**: `reserved_stock` → 0 (permanent deduction on payment)
4. **Adjust**: Direct modification of `available_stock` (admin operations)

## 🚀 Features

### Core Functionality
- ✅ **Stock Reservation**: Reserve stock for cart/order placement
- ✅ **Stock Release**: Release reserved stock when cart is cleared or order fails
- ✅ **Stock Deduction**: Permanently deduct stock on successful payment
- ✅ **Stock Adjustment**: Admin/manual stock adjustments
- ✅ **Inventory Creation**: Initialize inventory when products are created
- ✅ **Stock Queries**: Get stock information for products

### Advanced Features
- ✅ **Optimistic Locking**: Prevent race conditions with `@Version`
- ✅ **Transaction Management**: ACID compliance for all stock operations
- ✅ **Concurrency Control**: Handle concurrent access safely
- ✅ **Stock Analytics**: Low stock alerts and statistics
- ✅ **Comprehensive Logging**: Detailed operation logging
- ✅ **Exception Handling**: Proper error responses with retry guidance
- ✅ **API Documentation**: Complete OpenAPI/Swagger integration

## 📡 API Endpoints

All endpoints are prefixed with `/api/inventory` and called by other services through the API Gateway.

### Core Operations
```http
GET    /api/inventory                    # Get all inventory records
GET    /api/inventory/{productId}        # Get inventory for a product
POST   /api/inventory                    # Create inventory record
PUT    /api/inventory/reserve            # Reserve stock
PUT    /api/inventory/release            # Release reserved stock
PUT    /api/inventory/deduct             # Deduct reserved stock
PUT    /api/inventory/adjust             # Adjust stock (admin)
```

### Analytics & Monitoring
```http
GET    /api/inventory/low-stock          # Get low stock inventories
GET    /api/inventory/out-of-stock       # Get out of stock inventories
GET    /api/inventory/reserved           # Get inventories with reserved stock
GET    /api/inventory/stats              # Get inventory statistics
GET    /api/inventory/{productId}/available # Get available stock
GET    /api/inventory/{productId}/check  # Check stock availability
```

## 📋 API Contract Examples

### Create Inventory
```bash
curl -X POST "http://localhost:8085/api/inventory" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "initialStock": 100
  }'
```

**Response:**
```json
{
  "productId": 1,
  "availableStock": 100,
  "reservedStock": 0,
  "lastUpdated": "2024-01-15T10:30:00",
  "version": 0
}
```

### Reserve Stock
```bash
curl -X PUT "http://localhost:8085/api/inventory/reserve" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "referenceId": "cart-123"
  }'
```

**Success Response (200):**
```json
{
  "productId": 1,
  "reserved": 5,
  "available": 95,
  "operation": "RESERVE",
  "referenceId": "cart-123"
}
```

**Insufficient Stock Response (409):**
```json
{
  "status": 409,
  "error": "Insufficient Stock",
  "message": "Insufficient stock for product ID 1. Requested: 5, Available: 2",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/inventory/reserve"
}
```

### Release Stock
```bash
curl -X PUT "http://localhost:8085/api/inventory/release" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "referenceId": "cart-123"
  }'
```

### Deduct Stock
```bash
curl -X PUT "http://localhost:8085/api/inventory/deduct" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "orderId": 123
  }'
```

### Adjust Stock
```bash
curl -X PUT "http://localhost:8085/api/inventory/adjust" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "delta": 50,
    "reason": "Stock replenishment"
  }'
```

## 🔗 Service Integrations

### Called By Other Services

#### **Product Service**
- **Create Inventory**: When a new product is created
- **Get Stock Info**: For product availability checks

#### **Cart Service**
- **Reserve Stock**: When items are added to cart
- **Release Stock**: When items are removed from cart

#### **Order Service**
- **Reserve Stock**: During order placement
- **Deduct Stock**: On successful payment
- **Release Stock**: On order cancellation

### Integration Pattern
```java
// Example Feign Client Usage
@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {
    
    @PostMapping("/api/inventory")
    ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest request);
    
    @PutMapping("/api/inventory/reserve")
    ResponseEntity<StockOperationResponse> reserveStock(@RequestBody ReserveRequest request);
    
    @PutMapping("/api/inventory/release")
    ResponseEntity<StockOperationResponse> releaseStock(@RequestBody ReleaseRequest request);
    
    @PutMapping("/api/inventory/deduct")
    ResponseEntity<StockOperationResponse> deductStock(@RequestBody DeductRequest request);
    
    @GetMapping("/api/inventory/{productId}")
    ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId);
}
```

## 🛡️ Concurrency & Data Consistency

### Optimistic Locking
- **`@Version` Field**: Automatically incremented on each update
- **Conflict Detection**: `OptimisticLockingFailureException` on concurrent modifications
- **Retry Logic**: Services should retry operations on 409 Conflict responses

### Transaction Management
- **`@Transactional`**: All stock operations are transactional
- **Atomic Updates**: Stock changes are atomic (reserve/release/deduct)
- **Rollback**: Automatic rollback on exceptions

### Error Handling
- **409 Conflict**: Insufficient stock or optimistic locking failure
- **404 Not Found**: Inventory not found for product
- **400 Bad Request**: Invalid request data
- **500 Internal Server Error**: Unexpected failures

## 🛠️ Technology Stack

- **Java 17**: Modern Java features and performance
- **Spring Boot 3.5.6**: Rapid application development
- **Spring Data JPA**: Data persistence with optimistic locking
- **PostgreSQL**: Primary database with ACID compliance
- **H2**: In-memory database for testing
- **OpenAPI/Swagger**: API documentation
- **Actuator**: Application monitoring

## ⚙️ Configuration

### Application Properties
```properties
# Service Configuration
spring.application.name=inventory-service
server.port=8085

# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/ecom-db
spring.datasource.username=postgres
spring.datasource.password=system123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Eureka Service Discovery
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Logging Configuration
logging.level.com.ecom.inventoryservice=INFO
logging.level.org.springframework.web=DEBUG

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

## 🔧 Business Logic

### Stock Reservation Process
1. **Validate Request**: Check product ID and quantity
2. **Check Inventory**: Verify inventory exists for product
3. **Check Stock**: Ensure sufficient available stock
4. **Reserve Atomically**: Move stock from available to reserved
5. **Save Changes**: Persist with optimistic locking
6. **Return Response**: Confirm reservation with current stock levels

### Stock Release Process
1. **Validate Request**: Check product ID and quantity
2. **Check Inventory**: Verify inventory exists for product
3. **Check Reserved**: Ensure sufficient reserved stock
4. **Release Atomically**: Move stock from reserved to available
5. **Save Changes**: Persist with optimistic locking
6. **Return Response**: Confirm release with current stock levels

### Stock Deduction Process
1. **Validate Request**: Check product ID, quantity, and order ID
2. **Check Inventory**: Verify inventory exists for product
3. **Check Reserved**: Ensure sufficient reserved stock
4. **Deduct Atomically**: Remove stock from reserved (permanent)
5. **Save Changes**: Persist with optimistic locking
6. **Return Response**: Confirm deduction with current stock levels

## 📊 Monitoring and Analytics

### Inventory Statistics
```json
{
  "totalAvailableStock": 1500,
  "totalReservedStock": 250,
  "lowStockCount": 5,
  "outOfStockCount": 2,
  "reservedStockCount": 15,
  "totalStock": 1750
}
```

### Low Stock Alerts
- **Threshold**: Configurable low stock threshold (default: 10)
- **Monitoring**: Real-time low stock detection
- **Reporting**: List of products below threshold

### Health Checks
- **Database Connectivity**: PostgreSQL connection status
- **Service Health**: Overall service health
- **Metrics**: Stock operation metrics and performance

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

### Test Scenarios
- **Happy Path**: Successful reserve/release/deduct operations
- **Insufficient Stock**: Stock reservation with insufficient available stock
- **Optimistic Locking**: Concurrent modification scenarios
- **Edge Cases**: Zero stock, negative adjustments, invalid requests

## 🚀 Deployment

### Prerequisites
- Java 17+
- PostgreSQL database
- Eureka Server running
- API Gateway running

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/inventory-service-0.0.1-SNAPSHOT.jar

# Or run with Maven
mvn spring-boot:run
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔐 Security Considerations

- **API Gateway Integration**: All requests routed through API Gateway
- **No Authentication**: Service relies on Gateway for security
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: JPA parameterized queries
- **Optimistic Locking**: Prevents data corruption from concurrent access

## 📈 Performance Considerations

- **Optimistic Locking**: Minimal performance impact
- **Database Indexing**: Optimized queries with proper indexes
- **Connection Pooling**: Optimized database connections
- **Transaction Scope**: Minimal transaction boundaries
- **Caching**: Potential for Redis integration for read-heavy operations

## 🔄 Future Enhancements

- **Stock Alerts**: Real-time notifications for low stock
- **Batch Operations**: Bulk stock operations
- **Stock History**: Audit trail for all stock changes
- **Multi-Warehouse**: Support for multiple warehouse locations
- **Stock Forecasting**: Predictive stock management
- **Integration Events**: Event-driven stock updates

## 📞 Support

For issues, questions, or contributions:
- **Email**: support@ecommerce.com
- **Documentation**: [API Documentation](http://localhost:8085/swagger-ui.html)
- **Health Check**: [Service Health](http://localhost:8085/actuator/health)

---

**Inventory Service** - Managing stock with precision and reliability in the E-Commerce ecosystem.

package com.ecom.productservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "security.jwt.secret=test-secret-key-for-testing-purposes-only"
})
class ProductServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // with all the beans and configurations
    }
}
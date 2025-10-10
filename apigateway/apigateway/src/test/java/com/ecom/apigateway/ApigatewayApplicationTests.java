package com.ecom.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "eureka.client.enabled=false"
})
class ApigatewayApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // with all the beans and configurations
    }
}
package by.ratingsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RatingSystemApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedisConnection() {
        redisTemplate.opsForValue().set("test_key", "hello");
        String value = redisTemplate.opsForValue().get("test_key");
        assertEquals("hello", value, "Redis is unavailable or data was not saved");

        redisTemplate.delete("test_key");
        assertFalse(redisTemplate.hasKey("test_key"), "Key should not exist after deletion");
    }

    @Test
    void testH2Connection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn, "Connection should not be null");

            String dbProduct = conn.getMetaData().getDatabaseProductName();
            assertTrue(dbProduct.contains("H2"), "Database should be H2");
        }
    }
}
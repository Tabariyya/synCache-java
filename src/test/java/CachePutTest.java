import com.tabariyya.synCache.aop.CacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


import com.tabariyya.synCache.aop.annotations.CachePut;

import static org.junit.jupiter.api.Assertions.*;

class CachePutTest {

    private final TestService service = new TestService();

    @BeforeEach
    void clearCache() {
        CacheManager.initialize(System.getenv("BROKER_TOKEN"), 100);
        CacheManager.getInstance().evict("users");
    }

    @Test
    void cachePut_alwaysExecutesMethod() {
        int v1 = service.updateUser(1);
        int v2 = service.updateUser(1);

        assertEquals(1, v1);
        assertEquals(2, v2);
    }

    @Test
    void cachePut_updatesCacheValue() {
        service.updateUser(5);

        Integer cached = CacheManager.getInstance()
                .get("users", "5", Integer.class);

        assertEquals(1, cached);
    }

    @Test
    void cachePut_overwritesExistingValue() {
        service.updateUser(7);
        service.updateUser(7);

        Integer cached = CacheManager.getInstance()
                .get("users", "7", Integer.class);

        assertEquals(2, cached);
    }

    @Test
    void cachePut_keyExpressionUsesArgs() {
        service.updateUser(42);

        Integer cached = CacheManager.getInstance()
                .get("users", "42", Integer.class);

        assertEquals(1, cached);
    }

    @Test
    void cachePut_nullResultStillStored() {
        service.updateNullable(9);

        Integer cached = CacheManager.getInstance()
                .get("users", "9", Integer.class);

        assertNull(cached);
    }

    static class TestService {

        private int counter = 0;

        @CachePut(namespace = "users", key = "#id")
        public int updateUser(int id) {
            counter++;
            return counter;
        }

        @CachePut(namespace = "users", key = "#id")
        public Integer updateNullable(int id) {
            return null;
        }
    }
}

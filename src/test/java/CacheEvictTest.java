import com.tabariyya.synCache.aop.annotations.CacheEvict;
import com.tabariyya.synCache.aop.CacheManager;
import com.tabariyya.synCache.aop.annotations.Cacheable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheEvictTest {

    private final TestService service = new TestService();

    @BeforeEach
    void setup() {
        CacheManager.initialize(System.getenv("BROKER_TOKEN"), 100);
        CacheManager.getInstance().evict("users");
    }

    @Test
    void evict_removesSingleKey() {
        service.loadUser(1); // cached

        Integer before = CacheManager.getInstance().get("users", "1", Integer.class);
        assertEquals(1, before);

        service.evictUser(1);

        Integer after = CacheManager.getInstance().get("users", "1", Integer.class);
        assertNull(after);
    }

    @Test
    void evict_doesNotAffectOtherKeys() {
        service.loadUser(1);
        service.loadUser(2);

        service.evictUser(1);

        Integer remaining = CacheManager.getInstance().get("users", "2", Integer.class);

        assertEquals(2, remaining);
    }

    @Test
    void evict_allEntriesClearsNamespace() {
        service.loadUser(1);
        service.loadUser(2);

        service.evictAll();

        assertNull(CacheManager.getInstance().get("users", "1", Integer.class));
        assertNull(CacheManager.getInstance().get("users", "2", Integer.class));
    }

    @Test
    void evict_methodStillExecutes() {
        int v = service.evictAndReturn(5);
        assertEquals(5, v);
    }

    @Test
    void evict_keyFromArgs() {
        service.loadUser(9);

        service.evictUser(9);

        assertNull(CacheManager.getInstance().get("users", "9", Integer.class));
    }

    @Test
    void evict_keyFromResult() {
        service.loadUser(7);

        service.evictByResult(7);

        assertNull(CacheManager.getInstance().get("users", "7", Integer.class));
    }


    static class TestService {

        @Cacheable(namespace = "users", key = "#id")
        public int loadUser(int id) {
            return id;
        }

        @CacheEvict(namespace = "users", key = "#id")
        public void evictUser(int id) {
        }

        @CacheEvict(namespace = "users", allEntries = true)
        public void evictAll() {
        }

        @CacheEvict(namespace = "users", key = "#id")
        public int evictAndReturn(int id) {
            return id;
        }

        @CacheEvict(namespace = "users", key = "#result")
        public int evictByResult(int id) {
            return id;
        }

    }
}
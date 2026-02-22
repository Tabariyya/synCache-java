import com.tabariyya.synCache.annotations.CacheManager;
import com.tabariyya.synCache.annotations.Cacheable;
import models.Person;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class CacheableTest {

    static int callCount;

    static class Service {

        @Cacheable(namespace = "test_ns", key = "#userId")
        public Person getUser(int userId) {
            callCount++;
            return new Person("User" + userId, "Doe");
        }

        @Cacheable(namespace = "users", key = "#user.id")
        public String getUserDetails(Person user) {
            callCount++;
            return "Details for " + user.firstName + " " + user.lastName;
        }

        @Cacheable(namespace = "orders", key = "#user.id-#orderType")
        public String getOrder(Person user, String orderType) {
            callCount++;
            return user.id + ":" + orderType;
        }
    }

    Service svc;

    @BeforeEach
    void setup() {
        CacheManager.initialize("eyJhbGciOiJFUzI1NiJ9.eyJicm9rZXJVUkwiOiJ3c3M6Ly9icm9rZXIuc3luY2FjaGUudGFiYXJpeXlhLmNvbSIsImNvbXBhbnlOYW1lIjoiVGFiYXJpeXlhIiwiaW5zdGFuY2VOdW1iZXIiOjMwLCJwcm9qZWN0TmFtZSI6InN5bkNhY2hlIiwiZXhwIjoxODAyNzA3MjA1LCJ0eXBlIjoiSU5TVEFOQ0UiLCJpYXQiOjE3NzExNzEzMDB9.e_d0RDuYbA6nlfBJcM89ZSe6d9AQZiSOBYNYTRK8m7v2xSnXRoKFLQ02-j37mARp8HNmCMXoP3W-hhjUBs62iQ", 100);
        CacheManager.getInstance().evict();
        svc = new Service();
        callCount = 0;
    }

    @Test
    void testCacheableSimple() {
        Person r1 = svc.getUser(123);
        assertEquals(1, callCount);

        Person r2 = svc.getUser(123);
        assertEquals(1, callCount);

        Person r3 = svc.getUser(456);
        assertEquals(2, callCount);
    }

    @Test
    void testCacheableObjectParam() {
        Person u1 = new Person("John", "Doe", 1);
        Person u2 = new Person("Jane", "Smith", 2);

        svc.getUserDetails(u1);
        assertEquals(1, callCount);

        svc.getUserDetails(u1);
        assertEquals(1, callCount);

        svc.getUserDetails(u2);
        assertEquals(2, callCount);
    }

    @Test
    void testCacheableComplexKey() {
        Person u = new Person("Test", "User", 789);

        svc.getOrder(u, "pending");
        assertEquals(1, callCount);

        svc.getOrder(u, "pending");
        assertEquals(1, callCount);

        svc.getOrder(u, "completed");
        assertEquals(2, callCount);
    }
}

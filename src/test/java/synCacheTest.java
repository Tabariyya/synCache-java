import com.tabariyya.synCache.Cache;
import models.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class synCacheTest {

    String token = "eyJhbGciOiJFUzI1NiJ9.eyJicm9rZXJVUkwiOiJ3c3M6Ly9icm9rZXIuc3luY2FjaGUudGFiYXJpeXlhLmNvbSIsImNvbXBhbnlOYW1lIjoiVGFiYXJpeXlhIiwiaW5zdGFuY2VOdW1iZXIiOjMwLCJwcm9qZWN0TmFtZSI6InN5bkNhY2hlIiwiZXhwIjoxODAyNzA3MjA1LCJ0eXBlIjoiSU5TVEFOQ0UiLCJpYXQiOjE3NzExNzEzMDB9.e_d0RDuYbA6nlfBJcM89ZSe6d9AQZiSOBYNYTRK8m7v2xSnXRoKFLQ02-j37mARp8HNmCMXoP3W-hhjUBs62iQ";


    @Test
    void benchmarkSynCache() {
        Cache ctrl = new Cache(token, 100);
        User user = new User();
        user.setEmail("guest@guest");
        user.setUsername("guest");
        ctrl.set("ns", "1", user);
        for (int i = 0; i < 25000; i++) {
            ctrl.get("ns", "1", User.class);
        }
        Instant start = Instant.now();
        for (int i = 0; i < 25000; i++) {
            ctrl.get("ns", "1", User.class);
        }
        Instant end = Instant.now();
        System.out.println(ctrl.get("ns", "1", User.class).getEmail());
        Duration duration = Duration.between(start, end);
        System.out.println("Execution time: " + duration.toMillis() + " ms");

    }


}

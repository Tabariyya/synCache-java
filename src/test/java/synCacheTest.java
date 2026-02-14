import com.tabariyya.synCache.Cache;
import models.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class synCacheTest {

    String token = System.getenv("BROKER_TOKEN");


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

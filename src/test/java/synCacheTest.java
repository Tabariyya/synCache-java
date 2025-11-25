import io.github.WaleedSDA.synCache.CacheEntry;
import io.github.WaleedSDA.synCache.Controller;
import models.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class synCacheTest {

    String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjI2MjgwMjc2MDUsInN1YiI6Imdvb2dsZSJ9.XgGmtV7ffxFI_a_g6U7lT_6mn2hc7RvJhO3lukUgMhRflgA5UwwHPt-5c5-uF_wsyA3HPmwQg_cjvI_JrG122OHqbC7Y-16059W-r4W_QALEgHHZKcijf_5g1CsG4DjGfHYJI4JmwrogQ0_yj4UUCD6OMY5v5g0QH4FCsxWcaI4";


    @Test
    void benchmarkSynCache() {
        Controller ctrl = new Controller("ws://91.93.135.176:25672/", token, 100);
        User user = new User();
        user.setEmail("guest@guest");
        user.setUsername("guest");
        CacheEntry e = new CacheEntry("ns", "1", user);
        ctrl.set(e);
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

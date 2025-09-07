import com.synCache.CacheEntry;
import com.synCache.Controller;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

public class synCacheTest {

    @Test
    void test1() throws InterruptedException {
        Controller ctrl = new Controller("amqp://guest:guest@91.93.135.176:25672/", 100, false);
        CacheEntry<String> e = new CacheEntry<>("ns", "1", "value", null);
        ctrl.set(e);
        System.out.println("sleeping");
        Thread.sleep(10000);
        String v = ctrl.get("ns", "1", String.class);
        System.out.println("value=" + v);
        Thread.sleep(10000);

    }


    @Test
    void test2() {
        Controller ctrl = new Controller("amqp://guest:guest@91.93.135.176:25672/", 100, false);
        CacheEntry<String> e = new CacheEntry<>("ns", "1", "value", null);
        ctrl.set(e);


        Instant start = Instant.now();
        for (int i = 0; i < 25000; i++) {
            ctrl.get("ns", "1", String.class);

        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Execution time: " + duration.toMillis() + " ms");


    }
}

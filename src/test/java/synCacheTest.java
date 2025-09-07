import com.synCache.CacheEntry;
import com.synCache.Controller;
import org.junit.jupiter.api.Test;

public class synCacheTest {

    @Test
    void test1() throws InterruptedException {
        Controller ctrl = new Controller("amqp://guest:guest@91.93.135.176:25672/", 100);
        CacheEntry<String> e = new CacheEntry<>("ns", "1", "value", null);
        ctrl.set(e);
        System.out.println("sleeping");
        Thread.sleep(10000);
        String v = ctrl.get("ns", "1", String.class);
        System.out.println("value=" + v);
        Thread.sleep(10000);

    }
}

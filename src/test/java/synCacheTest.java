import com.synCache.cache.NativeCacheEntry;
import com.synCache.cache.NativeController;
import org.junit.jupiter.api.Test;

public class synCacheTest {

    @Test
    void test1() throws InterruptedException {
        NativeController ctrl = new NativeController("amqp://guest:guest@91.93.135.176:25672/", 100);
        NativeCacheEntry e = new NativeCacheEntry("1", "ns", "value", /*ttl*/ null);
        ctrl.set(e);
        System.out.println("sleeping");
        Thread.sleep(10000);
        String v = ctrl.get("ns", "1", String.class);
        System.out.println("value=" + v);
        Thread.sleep(10000);

    }
}

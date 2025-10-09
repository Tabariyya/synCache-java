import io.github.WaleedSDA.synCache.CacheEntry;
import io.github.WaleedSDA.synCache.Controller;
import models.User;
import org.junit.jupiter.api.Test;
//import org.redisson.Redisson;
//import org.redisson.api.LocalCachedMapOptions;
//import org.redisson.api.RLocalCachedMap;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;

import java.time.Duration;
import java.time.Instant;

public class synCacheTest {


    @Test
    void benchmarkSynCache() {
        Controller ctrl = new Controller("amqp://guest:guest@91.93.135.176:25672/", 100, false);
        User user = new User();
        user.setEmail("guest@guest");
        user.setUsername("guest");
        CacheEntry e = new CacheEntry("ns", "1", user, null);
        ctrl.set(e);
        for (int i = 0; i < 25000; i++) {
            ctrl.get("ns", "1", User.class);
        }
        Instant start = Instant.now();
        for (int i = 0; i < 25000; i++) {
            ctrl.get("ns", "1", User.class);
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Execution time: " + duration.toMillis() + " ms");


    }

//    @Test
//    void benchmarkRedisson() {
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
////        config.useSingleServer().setAddress("redis://host.docker.internal:6379");
//
//        RedissonClient redisson = Redisson.create(config);
//
//        // 2. Configure LocalCachedMap
//        LocalCachedMapOptions<Object, models.User> options = LocalCachedMapOptions.<Object, models.User>defaults()
//                .timeToLive(10, TimeUnit.SECONDS)
//                .maxIdle(5, TimeUnit.SECONDS)
//                .cacheSize(100)
//                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE);
//
//        RLocalCachedMap<Object, models.User> localCachedMap = redisson.getLocalCachedMap("myMap", options);
//
//        // 3. Put data into cache
//        models.User user = new models.User();
//        user.setEmail("guest@guest");
//        user.setUsername("guest");
//
//        localCachedMap.put("user:1", user);
//        for (int i = 0; i < 25000; i++) {
//            localCachedMap.get("user:1");
//        }
//
//
//        Instant start = Instant.now();
//        for (int i = 0; i < 25000; i++) {
//            localCachedMap.get("user:1");
//        }
//
//        Instant end = Instant.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println("Execution time: " + duration.toMillis() + " ms");
//        redisson.shutdown();
//    }


}

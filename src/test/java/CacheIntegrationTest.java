
import com.tabariyya.synCache.Cache;
import models.ComplexObject;
import models.User;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CacheIntegrationTest {

    private static final String TEST_TOKEN = System.getenv("BROKER_TOKEN");

    private Cache cache;

    @BeforeEach
    void setUp() {
        assumeTrue(TEST_TOKEN != null && !TEST_TOKEN.isEmpty(),
                "BROKER_TOKEN environment variable must be set for integration tests");

        cache = new Cache(TEST_TOKEN, 1000);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        if (cache != null) {
            cache.evict();
        }
    }

    @Test
    @DisplayName("Basic set and get operation")
    void testBasicSetAndGet() {
        // Given
        String namespace = "test-ns";
        String key = "key1";
        String value = "Hello, World!";

        // When
        cache.set(namespace, key, value);
        String retrieved = cache.get(namespace, key, String.class);

        // Then
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    @DisplayName("Set and get with TTL")
    void testSetAndGetWithTTL() throws InterruptedException {
        // Given
        String namespace = "ttl-test";
        String key = "key-ttl";
        String value = "TTL Test";
        Long ttl = 1L; // 1 second

        // When
        cache.set(namespace, key, value, ttl);
        String retrieved = cache.get(namespace, key, String.class);

        // Then - Should be available immediately
        assertNotNull(retrieved);
        assertEquals(value, retrieved);

        // Wait for TTL to expire
        Thread.sleep(2000);

        // Should be expired
        String expired = cache.get(namespace, key, String.class);
        assertNull(expired, "Value should be expired after TTL");
    }

    @Test
    @DisplayName("Set and get with custom object")
    void testSetAndGetCustomObject() {
        // Given
        String namespace = "object-test";
        String key = "user-1";
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setAge(30);

        // When
        cache.set(namespace, key, user);
        User retrieved = cache.get(namespace, key, User.class);

        // Then
        assertNotNull(retrieved);
        assertEquals(user.getEmail(), retrieved.getEmail());
        assertEquals(user.getUsername(), retrieved.getUsername());
        assertEquals(user.getAge(), retrieved.getAge());
    }

    @Test
    @DisplayName("Set and get without TTL (infinite)")
    void testSetAndGetWithoutTTL() throws InterruptedException {
        // Given
        String namespace = "infinite-test";
        String key = "key-infinite";
        String value = "No TTL";

        // When
        cache.set(namespace, key, value); // No TTL specified

        // Wait a bit
        Thread.sleep(100);

        // Then - Should still be available
        String retrieved = cache.get(namespace, key, String.class);
        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

    @Test
    @DisplayName("Evict specific key")
    void testEvictSpecificKey() {
        // Given
        String namespace = "evict-test";
        String key1 = "key1";
        String key2 = "key2";

        cache.set(namespace, key1, "value1");
        cache.set(namespace, key2, "value2");

        // When - Retrieve before eviction
        String beforeEvict1 = cache.get(namespace, key1, String.class);
        String beforeEvict2 = cache.get(namespace, key2, String.class);

        // Evict key1
        cache.evict(namespace, key1);

        // Retrieve after eviction
        String afterEvict1 = cache.get(namespace, key1, String.class);
        String afterEvict2 = cache.get(namespace, key2, String.class);

        // Then
        assertNotNull(beforeEvict1);
        assertNotNull(beforeEvict2);
        assertNull(afterEvict1, "Key1 should be evicted");
        assertNotNull(afterEvict2, "Key2 should still be available");
        assertEquals("value2", afterEvict2);
    }

    @Test
    @DisplayName("Evict all keys in namespace")
    void testEvictNamespace() {
        // Given
        String namespace1 = "ns1";
        String namespace2 = "ns2";

        cache.set(namespace1, "key1", "value1");
        cache.set(namespace1, "key2", "value2");
        cache.set(namespace2, "key1", "value3");

        // When - Evict namespace1
        cache.evict(namespace1);

        // Then
        assertNull(cache.get(namespace1, "key1", String.class));
        assertNull(cache.get(namespace1, "key2", String.class));
        assertNotNull(cache.get(namespace2, "key1", String.class));
    }

    @Test
    @DisplayName("Evict all keys")
    void testEvictAll() {
        // Given
        cache.set("ns1", "key1", "value1");
        cache.set("ns1", "key2", "value2");
        cache.set("ns2", "key1", "value3");
        cache.set("ns2", "key2", "value4");

        // When
        cache.evict();

        // Then
        assertNull(cache.get("ns1", "key1", String.class));
        assertNull(cache.get("ns1", "key2", String.class));
        assertNull(cache.get("ns2", "key1", String.class));
        assertNull(cache.get("ns2", "key2", String.class));
    }

    @Test
    @DisplayName("Get non-existent key returns null")
    void testGetNonExistentKey() {
        // When
        String result = cache.get("non-existent", "key", String.class);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Update existing key")
    void testUpdateExistingKey() {
        // Given
        String namespace = "update-test";
        String key = "key1";

        cache.set(namespace, key, "value1");
        String first = cache.get(namespace, key, String.class);

        // When - Update with new value
        cache.set(namespace, key, "value2");
        String second = cache.get(namespace, key, String.class);

        // Then
        assertEquals("value1", first);
        assertEquals("value2", second);
    }

    @Test
    @DisplayName("Handle different data types")
    void testDifferentDataTypes() {
        String namespace = "types-test";

        // Test String
        cache.set(namespace, "string", "Hello");
        assertEquals("Hello", cache.get(namespace, "string", String.class));

        // Test Integer
        cache.set(namespace, "integer", 42);
        assertEquals(42, (int) cache.get(namespace, "integer", Integer.class));

        // Test Boolean
        cache.set(namespace, "boolean", true);
        assertTrue(cache.get(namespace, "boolean", Boolean.class));

        // Test Double
        cache.set(namespace, "double", 3.14159);
        assertEquals(3.14159, cache.get(namespace, "double", Double.class), 0.00001);

        // Test Array
        int[] array = {1, 2, 3, 4, 5};
        cache.set(namespace, "array", array);
        int[] retrievedArray = cache.get(namespace, "array", int[].class);
        assertArrayEquals(array, retrievedArray);
    }

    @Test
    @DisplayName("Handle large values")
    void testLargeValues() {
        // Given
        String namespace = "large-test";
        String key = "large-key";

        // Create a large string
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeString.append("Line ").append(i).append("\n");
        }

        // When
        cache.set(namespace, key, largeString.toString());
        String retrieved = cache.get(namespace, key, String.class);

        // Then
        assertNotNull(retrieved);
        assertEquals(largeString.toString(), retrieved);
    }

    @Test
    @DisplayName("Handle many keys")
    void testManyKeys() {
        String namespace = "many-keys";
        int keyCount = 1000;

        // Insert many keys
        for (int i = 0; i < keyCount; i++) {
            cache.set(namespace, "key-" + i, "value-" + i);
        }

        // Verify all keys
        for (int i = 0; i < keyCount; i++) {
            String value = cache.get(namespace, "key-" + i, String.class);
            assertNotNull(value, "Key-" + i + " should exist");
            assertEquals("value-" + i, value);
        }
    }

    @Test
    @DisplayName("Concurrent access from multiple threads")
    void testConcurrentAccess() throws InterruptedException {
        String namespace = "concurrent-test";
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        String key = "thread-" + threadId + "-key-" + i;
                        String value = "thread-" + threadId + "-value-" + i;

                        // Set value
                        cache.set(namespace, key, value);

                        // Get value
                        String retrieved = cache.get(namespace, key, String.class);

                        if (value.equals(retrieved)) {
                            successCount.incrementAndGet();
                        }

                        // Evict every 10th key
                        if (i % 10 == 0) {
                            cache.evict(namespace, key);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        int totalOperations = threadCount * operationsPerThread;
        System.out.println("Concurrent test completed: " + successCount.get() +
                "/" + totalOperations + " successful operations");

        assertTrue(successCount.get() > totalOperations * 0.9,
                "At least 90% of operations should succeed");
    }

    @Test
    @DisplayName("Performance benchmark - 25,000 gets")
    void benchmarkPerformance() {
        // Given
        String namespace = "benchmark";
        String key = "bench-key";
        User user = new User();
        user.setEmail("bench@test.com");
        user.setUsername("benchuser");

        // Warm up
        cache.set(namespace, key, user);
        for (int i = 0; i < 1000; i++) {
            cache.get(namespace, key, User.class);
        }

        // Benchmark
        Instant start = Instant.now();
        for (int i = 0; i < 25000; i++) {
            User retrieved = cache.get(namespace, key, User.class);
            assertNotNull(retrieved);
        }
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("25,000 gets completed in: " + duration.toMillis() + " ms");
        System.out.println("Average time per get: " + (duration.toMillis() / 25000.0) + " ms");

        // Performance assertion
        assertTrue(duration.toMillis() < 5000, "Should complete under 5 seconds");
    }

    @Test
    @DisplayName("Edge case - empty strings")
    void testEmptyStrings() {
        // These might or might not work depending on native implementation
        assertDoesNotThrow(() -> {
            cache.set("", "", "empty");
            cache.get("", "", String.class);
        });
    }

    @Test
    @DisplayName("Edge case - special characters in keys")
    void testSpecialCharacters() {
        String namespace = "special!@#$%^&*()";
        String key = "key{}\":<>?[]\\|;',./`~";
        String value = "special value";

        cache.set(namespace, key, value);
        String retrieved = cache.get(namespace, key, String.class);

        assertNotNull(retrieved);
        assertEquals(value, retrieved);
    }

//    @Test
//    @DisplayName("Cache respects max entries limit")
//    void testMaxEntriesLimit() {
//        // Create cache with small limit
//        Cache smallCache = new Cache(TEST_BROKER_URL, TEST_TOKEN, 10);
//        String namespace = "limit-test";
//
//        try {
//            // Insert more entries than the limit
//            for (int i = 0; i < 20; i++) {
//                smallCache.set(namespace, "key-" + i, "value-" + i);
//            }
//
//            // Some keys might be evicted by LRU policy
//            int foundCount = 0;
//            for (int i = 0; i < 20; i++) {
//                if (smallCache.get(namespace, "key-" + i, String.class) != null) {
//                    foundCount++;
//                }
//            }
//
//            System.out.println("Found " + foundCount + " out of 20 inserted keys with limit 10");
//
//            // Should not exceed limit (plus some buffer for implementation details)
//            assertTrue(foundCount <= 15, "Should respect max entries limit");
//        } finally {
//            smallCache.evict();
//        }
//    }

    @Test
    @DisplayName("Test serialization/deserialization with complex object")
    void testComplexObject() {
        String namespace = "complex-test";
        String key = "complex-key";

        ComplexObject complex = new ComplexObject();
        complex.setName("Test Object");
        complex.setValue(12345);
        complex.setData(new String[]{"one", "two", "three"});
        complex.setNested(new User());
        complex.getNested().setUsername("nested");
        complex.getNested().setEmail("nested@test.com");

        cache.set(namespace, key, complex);
        ComplexObject retrieved = cache.get(namespace, key, ComplexObject.class);

        assertNotNull(retrieved);
        assertEquals(complex.getName(), retrieved.getName());
        assertEquals(complex.getValue(), retrieved.getValue());
        assertArrayEquals(complex.getData(), retrieved.getData());
        assertNotNull(retrieved.getNested());
        assertEquals(complex.getNested().getUsername(), retrieved.getNested().getUsername());
        assertEquals(complex.getNested().getEmail(), retrieved.getNested().getEmail());
    }


    @Test
    @DisplayName("Stress test with mixed operations")
    void stressTest() {
        String namespace = "stress-test";
        int operations = 5000;
        Instant start = Instant.now();

        for (int i = 0; i < operations; i++) {
            String key = "stress-" + i;
            String value = "value-" + i;

            // Set
            cache.set(namespace, key, value);

            // Get
            String retrieved = cache.get(namespace, key, String.class);
            assertEquals(value, retrieved);

            // Update
            if (i % 3 == 0) {
                cache.set(namespace, key, value + "-updated");
            }

            // Evict some
            if (i % 7 == 0) {
                cache.evict(namespace, key);
            }
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        System.out.println("Stress test completed " + operations + " operations in " +
                duration.toMillis() + " ms");
        System.out.println("Operations per second: " +
                (operations * 1000.0 / duration.toMillis()));
    }
}
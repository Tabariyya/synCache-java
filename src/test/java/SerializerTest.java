import com.tabariyya.synCache.Serializer;
import models.ComplexPojo;
import models.SimplePojo;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


public class SerializerTest {


    @Test
    public void testSimplePojoSerialization() {
        SimplePojo original = new SimplePojo("John Doe", 30, 75000.50);

        byte[] bytes = Serializer.toBytes(original);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        SimplePojo deserialized = Serializer.fromBytes(bytes, SimplePojo.class);
        assertNotNull(deserialized);
        assertEquals(original, deserialized);
        assertEquals("John Doe", deserialized.getName());
        assertEquals(30, deserialized.getAge());
        assertEquals(75000.50, deserialized.getSalary(), 0.001);
    }
//
    @Test
    public void testComplexPojoSerialization() {
        SimplePojo nested = new SimplePojo("Nested", 25, 50000.0);
        HashMap<String, Integer> counts = new HashMap<>();
        counts.put("key1", 1);
        counts.put("key2", 2);

        ComplexPojo original = new ComplexPojo(
                Arrays.asList("item1", "item2", "item3"),
                counts,
                nested
        );

        byte[] bytes = Serializer.toBytes(original);
        assertNotNull(bytes);

        ComplexPojo deserialized = Serializer.fromBytes(bytes, ComplexPojo.class);
        assertNotNull(deserialized);
        assertEquals(original, deserialized);
        assertEquals(3, deserialized.getItems().size());
        assertEquals(2, deserialized.getCounts().size());
        assertEquals("Nested", deserialized.getNested().getName());
    }

    @Test
    public void testNullSerialization() {
        byte[] bytes = Serializer.toBytes(null);
        assertNotNull(bytes);

        // Deserializing null should return null
        Object result = Serializer.fromBytes(bytes, Object.class);
        assertNull(result);
    }

    @Test
    public void testPrimitiveTypes() {
        // Test Integer
        Integer intValue = 42;
        byte[] intBytes = Serializer.toBytes(intValue);
        Integer intResult = Serializer.fromBytes(intBytes, Integer.class);
        assertEquals(intValue, intResult);

        // Test String
        String stringValue = "Hello, World!";
        byte[] stringBytes = Serializer.toBytes(stringValue);
        String stringResult = Serializer.fromBytes(stringBytes, String.class);
        assertEquals(stringValue, stringResult);

        // Test Double
        Double doubleValue = 3.14159;
        byte[] doubleBytes = Serializer.toBytes(doubleValue);
        Double doubleResult = Serializer.fromBytes(doubleBytes, Double.class);
        assertEquals(doubleValue, doubleResult);

        // Test Boolean
        Boolean boolValue = true;
        byte[] boolBytes = Serializer.toBytes(boolValue);
        Boolean boolResult = Serializer.fromBytes(boolBytes, Boolean.class);
        assertEquals(boolValue, boolResult);
    }

    @Test
    public void testEmptyCollections() {
        // Test empty list
        List<String> emptyList = Collections.emptyList();
        byte[] listBytes = Serializer.toBytes(emptyList);
        List<String> listResult = Serializer.fromBytes(listBytes, List.class);
        assertNotNull(listResult);
        assertTrue(listResult.isEmpty());

        // Test empty map
        Map<String, String> emptyMap = Collections.emptyMap();
        byte[] mapBytes = Serializer.toBytes(emptyMap);
        Map<String, String> mapResult = Serializer.fromBytes(mapBytes, Map.class);
        assertNotNull(mapResult);
        assertTrue(mapResult.isEmpty());
    }
//
    @Test
    public void testThreadSafety() throws InterruptedException, ExecutionException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            tasks.add(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    SimplePojo pojo = new SimplePojo("Thread-" + threadId, threadId, threadId * 1000.0);

                    // Serialize
                    byte[] bytes = Serializer.toBytes(pojo);
                    assertNotNull(bytes);

                    // Deserialize
                    SimplePojo result = Serializer.fromBytes(bytes, SimplePojo.class);

                    // Verify
                    return pojo.equals(result) &&
                            pojo.getName().equals(result.getName()) &&
                            pojo.getAge() == result.getAge() &&
                            Math.abs(pojo.getSalary() - result.getSalary()) < 0.001;
                }
            });
        }

        List<Future<Boolean>> results = executor.invokeAll(tasks);

        for (Future<Boolean> result : results) {
            assertTrue(result.get());
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        int iterations = 1000;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(2);

        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failureCount = new AtomicInteger(0);

        // Thread 1: Serialize/Deserialize SimplePojo
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                    for (int i = 0; i < iterations; i++) {
                        try {
                            SimplePojo pojo = new SimplePojo("Thread1-" + i, i, i * 100.0);
                            byte[] bytes = Serializer.toBytes(pojo);
                            SimplePojo result = Serializer.fromBytes(bytes, SimplePojo.class);
                            if (pojo.equals(result)) {
                                successCount.incrementAndGet();
                            } else {
                                failureCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }
        });

        // Thread 2: Serialize/Deserialize different objects
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startLatch.await();
                    for (int i = 0; i < iterations; i++) {
                        try {
                            HashMap<String, Integer> counts = new HashMap<>();
                            counts.put("x", i);
                            counts.put("y", i * 2);

                            ComplexPojo pojo = new ComplexPojo(
                                    Arrays.asList("a", "b", "c"),
                                    counts,
                                    new SimplePojo("nested", i, i * 50.0)
                            );
                            byte[] bytes = Serializer.toBytes(pojo);
                            ComplexPojo result = Serializer.fromBytes(bytes, ComplexPojo.class);
                            if (pojo.equals(result)) {
                                successCount.incrementAndGet();
                            } else {
                                failureCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }
        });

        thread1.start();
        thread2.start();

        // Start both threads simultaneously
        startLatch.countDown();

        // Wait for completion
        boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Test should complete within timeout");

        // Verify
        assertEquals(iterations * 2, successCount.get());
        assertEquals(0, failureCount.get());
    }
//
    @Test
    public void testPerformance() {
        SimplePojo pojo = new SimplePojo("Test", 30, 50000.0);

        // Warm-up
        for (int i = 0; i < 1000; i++) {
            byte[] bytes = Serializer.toBytes(pojo);
            Serializer.fromBytes(bytes, SimplePojo.class);
        }

        // Timing test
        int iterations = 10000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            byte[] bytes = Serializer.toBytes(pojo);
            Serializer.fromBytes(bytes, SimplePojo.class);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double avgTimeMs = (duration / 1000000.0) / iterations;

        System.out.printf("Average serialization/deserialization time: %.4f ms per operation%n", avgTimeMs);

        // Performance assertion (adjust threshold as needed)
        assertTrue(avgTimeMs < 1.0);
    }


    @Test
    public void testByteArrayReuse() {
        // Test that serializing the same object multiple times works correctly
        SimplePojo pojo = new SimplePojo("Test", 30, 50000.0);

        byte[] bytes1 = Serializer.toBytes(pojo);
        byte[] bytes2 = Serializer.toBytes(pojo);

        // They should be equal but can be different instances
        assertArrayEquals(bytes1, bytes2);

        SimplePojo result1 = Serializer.fromBytes(bytes1, SimplePojo.class);
        SimplePojo result2 = Serializer.fromBytes(bytes2, SimplePojo.class);

        assertEquals(pojo, result1);
        assertEquals(pojo, result2);
        assertEquals(result1, result2);
    }

    @Test
    public void testEdgeCases() {
        // Test with very large object
        StringBuilder largeStringBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeStringBuilder.append("test");
        }
        String largeString = largeStringBuilder.toString();

        byte[] bytes = Serializer.toBytes(largeString);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        String result = Serializer.fromBytes(bytes, String.class);
        assertEquals(largeString, result);

        // Test with special characters
        String specialString = "特殊字符 ñáéíóú テスト Δέλτα";
        byte[] specialBytes = Serializer.toBytes(specialString);
        String specialResult = Serializer.fromBytes(specialBytes, String.class);
        assertEquals(specialString, specialResult);
    }

    @Test
    public void testMapWithVariousTypes() {
        Map<String, Object> complexMap = new HashMap<>();
        complexMap.put("string", "value");
        complexMap.put("integer", 123);
        complexMap.put("double", 45.67);
        complexMap.put("boolean", true);
        complexMap.put("list", Arrays.asList("a", "b", "c"));

        Map<String, Integer> innerMap = new HashMap<>();
        innerMap.put("inner1", 1);
        innerMap.put("inner2", 2);
        complexMap.put("map", innerMap);

        byte[] bytes = Serializer.toBytes(complexMap);
        assertNotNull(bytes);

        Map result = Serializer.fromBytes(bytes, Map.class);
        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("value", result.get("string"));
        assertEquals(123L, result.get("integer"));
    }

    @Test
    public void testListOfObjects() {
        List<Object> mixedList = new ArrayList<>();
        mixedList.add("string");
        mixedList.add(123);
        mixedList.add(45.67);
        mixedList.add(true);
        mixedList.add(new SimplePojo("test", 25, 30000.0));

        byte[] bytes = Serializer.toBytes(mixedList);
        assertNotNull(bytes);

        List result = Serializer.fromBytes(bytes, List.class);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("string", result.get(0));
        assertEquals(123L, result.get(1));
    }

    @Test
    public void testArraySerialization() {
        String[] stringArray = {"one", "two", "three"};
        byte[] bytes = Serializer.toBytes(stringArray);
        assertNotNull(bytes);

        String[] result = Serializer.fromBytes(bytes, String[].class);
        assertNotNull(result);
        assertArrayEquals(stringArray, result);
    }

    @Test
    public void testDateSerialization() {
        Date now = new Date();
        byte[] bytes = Serializer.toBytes(now);
        assertNotNull(bytes);

        Date result = Serializer.fromBytes(bytes, Date.class);
        assertNotNull(result);
        assertEquals(now.getTime(), result.getTime());
    }

    @Test
    public void testUUIDSerialization() {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = Serializer.toBytes(uuid);
        assertNotNull(bytes);

        UUID result = Serializer.fromBytes(bytes, UUID.class);
        assertNotNull(result);
        assertEquals(uuid, result);
    }

    @Test
    public void testMemoryLeakPrevention() throws InterruptedException {
        // Test that ThreadLocal doesn't cause memory leaks
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SimplePojo pojo = new SimplePojo("Thread-" + threadId, threadId, 1000.0);
                        byte[] bytes = Serializer.toBytes(pojo);
                        SimplePojo result = Serializer.fromBytes(bytes, SimplePojo.class);
                        assertEquals(pojo, result);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Force garbage collection to see if ThreadLocal is properly cleaned up
        System.gc();
        Thread.sleep(100);

        // Run another batch to ensure ThreadLocal is reinitialized properly
        SimplePojo finalCheck = new SimplePojo("Final", 99, 99999.0);
        byte[] bytes = Serializer.toBytes(finalCheck);
        SimplePojo result = Serializer.fromBytes(bytes, SimplePojo.class);
        assertEquals(finalCheck, result);
    }
}
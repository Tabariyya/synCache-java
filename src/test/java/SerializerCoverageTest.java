import com.tabariyya.synCache.Serializer;
import models.FinancialRecord;
import models.FinancialRecord.Status;
import models.SimplePojo;
import models.TemporalPojo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Covers every type family not exercised by SerializerTest:
 * java.time (all 9), BigDecimal/BigInteger, Long/Float/Short/Byte,
 * enums, null-field POJOs, boundary numerics, nested collections,
 * List<POJO>, Map<String,POJO>, primitive arrays, TemporalPojo in-POJO,
 * FinancialRecord in-POJO, ZonedDateTime across time zones.
 */
public class SerializerCoverageTest {

    // ── java.time individual types ─────────────────────────────────────────

    @Test
    public void testLocalDate() {
        LocalDate original = LocalDate.of(2024, 3, 15);
        LocalDate result = roundTrip(original, LocalDate.class);
        assertEquals(original, result);
    }

    @Test
    public void testLocalTime() {
        LocalTime original = LocalTime.of(13, 45, 59, 123_000_000);
        LocalTime result = roundTrip(original, LocalTime.class);
        assertEquals(original, result);
    }

    @Test
    public void testLocalDateTime() {
        LocalDateTime original = LocalDateTime.of(2024, 6, 21, 10, 30, 0, 0);
        LocalDateTime result = roundTrip(original, LocalDateTime.class);
        assertEquals(original, result);
    }

    @Test
    public void testOffsetTime() {
        OffsetTime original = OffsetTime.of(14, 0, 0, 0, ZoneOffset.ofHours(3));
        OffsetTime result = roundTrip(original, OffsetTime.class);
        assertEquals(original, result);
    }

    @Test
    public void testOffsetDateTime() {
        OffsetDateTime original = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime result = roundTrip(original, OffsetDateTime.class);
        assertEquals(original, result);
    }

    @Test
    public void testZonedDateTimeUtc() {
        ZonedDateTime original = ZonedDateTime.of(2024, 7, 4, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime result = roundTrip(original, ZonedDateTime.class);
        assertEquals(original, result);
    }

    @Test
    public void testZonedDateTimeAcrossZones() {
        for (String zoneId : new String[]{"America/New_York", "Asia/Tokyo", "Europe/Berlin", "Pacific/Auckland"}) {
            ZonedDateTime original = ZonedDateTime.now(ZoneId.of(zoneId)).truncatedTo(ChronoUnit.MILLIS);
            ZonedDateTime result = roundTrip(original, ZonedDateTime.class);
            assertEquals(original, result, "Failed for zone: " + zoneId);
        }
    }

    @Test
    public void testDuration() {
        Duration original = Duration.ofHours(2).plusMinutes(30).plusSeconds(15).plusMillis(500);
        Duration result = roundTrip(original, Duration.class);
        assertEquals(original, result);
    }

    @Test
    public void testDurationNegative() {
        Duration original = Duration.ofMillis(-7500);
        Duration result = roundTrip(original, Duration.class);
        assertEquals(original, result);
    }

    @Test
    public void testDurationZero() {
        Duration result = roundTrip(Duration.ZERO, Duration.class);
        assertEquals(Duration.ZERO, result);
    }

    @Test
    public void testPeriod() {
        Period original = Period.of(1, 6, 15);
        Period result = roundTrip(original, Period.class);
        assertEquals(original, result);
    }

    @Test
    public void testPeriodZero() {
        Period result = roundTrip(Period.ZERO, Period.class);
        assertEquals(Period.ZERO, result);
    }

    @Test
    public void testPeriodNegative() {
        Period original = Period.of(-2, -3, -10);
        Period result = roundTrip(original, Period.class);
        assertEquals(original, result);
    }

    // ── TemporalPojo — all 9 java.time fields in one object ───────────────

    @Test
    public void testTemporalPojoAllFields() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        TemporalPojo original = new TemporalPojo(
                LocalDate.of(2025, 12, 31),
                LocalTime.of(23, 59, 59, 999_000_000),
                LocalDateTime.of(2025, 6, 15, 8, 0, 0),
                OffsetTime.of(9, 30, 0, 0, ZoneOffset.ofHours(5)),
                OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-5)),
                ZonedDateTime.of(2025, 3, 20, 12, 0, 0, 0, ZoneId.of("Asia/Tokyo")),
                now,
                Duration.ofDays(3).plusHours(4),
                Period.of(0, 2, 14)
        );
        TemporalPojo result = roundTrip(original, TemporalPojo.class);
        assertEquals(original, result);
    }

    @Test
    public void testTemporalPojoNullFields() {
        TemporalPojo original = new TemporalPojo(
                LocalDate.of(2024, 1, 1), null, null, null, null, null,
                Instant.now().truncatedTo(ChronoUnit.MILLIS), null, null
        );
        TemporalPojo result = roundTrip(original, TemporalPojo.class);
        assertEquals(original.getDate(), result.getDate());
        assertEquals(original.getInstant(), result.getInstant());
        assertNull(result.getTime());
        assertNull(result.getDuration());
    }

    // ── BigDecimal / BigInteger ────────────────────────────────────────────

    @Test
    public void testBigDecimalPrecision() {
        BigDecimal original = new BigDecimal("123456789.987654321");
        BigDecimal result = roundTrip(original, BigDecimal.class);
        assertEquals(0, original.compareTo(result));
    }

    @Test
    public void testBigDecimalScalePreserved() {
        BigDecimal original = new BigDecimal("1.00");
        BigDecimal result = roundTrip(original, BigDecimal.class);
        assertEquals(original.scale(), result.scale());
    }

    @Test
    public void testBigDecimalNegative() {
        BigDecimal original = new BigDecimal("-9999999999.999999");
        BigDecimal result = roundTrip(original, BigDecimal.class);
        assertEquals(0, original.compareTo(result));
    }

    @Test
    public void testBigDecimalZero() {
        BigDecimal result = roundTrip(BigDecimal.ZERO, BigDecimal.class);
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    public void testBigInteger() {
        BigInteger original = new BigInteger("99999999999999999999999999999999");
        BigInteger result = roundTrip(original, BigInteger.class);
        assertEquals(original, result);
    }

    @Test
    public void testBigIntegerNegative() {
        BigInteger original = new BigInteger("-123456789012345678901234567890");
        BigInteger result = roundTrip(original, BigInteger.class);
        assertEquals(original, result);
    }

    // ── Numeric primitives not covered by SerializerTest ──────────────────

    @Test
    public void testLong() {
        Long original = Long.MAX_VALUE;
        assertEquals(original, roundTrip(original, Long.class));

        original = Long.MIN_VALUE;
        assertEquals(original, roundTrip(original, Long.class));

        original = 0L;
        assertEquals(original, roundTrip(original, Long.class));
    }

    @Test
    public void testFloat() {
        Float original = 3.14159f;
        assertEquals(original, roundTrip(original, Float.class), 0.00001f);
    }

    @Test
    public void testShort() {
        Short original = Short.MAX_VALUE;
        assertEquals(original, roundTrip(original, Short.class));

        original = Short.MIN_VALUE;
        assertEquals(original, roundTrip(original, Short.class));
    }

    @Test
    public void testByte() {
        Byte original = Byte.MAX_VALUE;
        assertEquals(original, roundTrip(original, Byte.class));

        original = Byte.MIN_VALUE;
        assertEquals(original, roundTrip(original, Byte.class));
    }

    @Test
    public void testNegativeAndZeroIntegers() {
        assertEquals(Integer.MIN_VALUE, (int) roundTrip(Integer.MIN_VALUE, Integer.class));
        assertEquals(Integer.MAX_VALUE, (int) roundTrip(Integer.MAX_VALUE, Integer.class));
        assertEquals(0, (int) roundTrip(0, Integer.class));
    }

    // ── Enum ──────────────────────────────────────────────────────────────

    @Test
    public void testEnumAllValues() {
        for (Status status : Status.values()) {
            Status result = roundTrip(status, Status.class);
            assertEquals(status, result);
        }
    }

    // ── FinancialRecord — BigDecimal + BigInteger + Long + Enum in one POJO ─

    @Test
    public void testFinancialRecord() {
        FinancialRecord original = new FinancialRecord(
                1_000_000L,
                new BigDecimal("9999.99"),
                new BigDecimal("199.99"),
                new BigInteger("987654321098765432"),
                Status.SETTLED,
                "USD",
                Arrays.asList(new BigDecimal("100.00"), new BigDecimal("200.50"), new BigDecimal("9699.49")),
                new LinkedHashMap<String, BigDecimal>() {{
                    put("subtotal", new BigDecimal("9999.99"));
                    put("discount", new BigDecimal("-200.00"));
                    put("shipping", new BigDecimal("0.00"));
                }}
        );
        FinancialRecord result = roundTrip(original, FinancialRecord.class);
        assertEquals(original, result);
        assertEquals(Status.SETTLED, result.getStatus());
        assertEquals(0, new BigDecimal("9999.99").compareTo(result.getAmount()));
    }

    @Test
    public void testFinancialRecordNullFields() {
        FinancialRecord original = new FinancialRecord(
                42L, new BigDecimal("0.00"), null,
                null, Status.PENDING, "EUR", null, null
        );
        FinancialRecord result = roundTrip(original, FinancialRecord.class);
        assertEquals(original.getId(), result.getId());
        assertEquals(Status.PENDING, result.getStatus());
        assertNull(result.getTax());
        assertNull(result.getLineItems());
    }

    // ── Nested / deep collections ─────────────────────────────────────────

    @Test
    public void testListOfLists() {
        List<List<Integer>> original = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9)
        );
        List result = roundTrip(original, List.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, ((List<?>) result.get(0)).size());
    }

    @Test
    public void testMapOfLists() {
        Map<String, List<String>> original = new LinkedHashMap<>();
        original.put("fruits", Arrays.asList("apple", "banana", "cherry"));
        original.put("veggies", Arrays.asList("carrot", "spinach"));
        original.put("empty", Collections.emptyList());

        Map result = roundTrip(original, Map.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, ((List<?>) result.get("fruits")).size());
        assertTrue(((List<?>) result.get("empty")).isEmpty());
    }

    @Test
    public void testMapOfMaps() {
        Map<String, Map<String, Integer>> original = new LinkedHashMap<>();
        Map<String, Integer> inner1 = new LinkedHashMap<>();
        inner1.put("a", 1); inner1.put("b", 2);
        Map<String, Integer> inner2 = new LinkedHashMap<>();
        inner2.put("x", 10); inner2.put("y", 20);
        original.put("group1", inner1);
        original.put("group2", inner2);

        Map result = roundTrip(original, Map.class);
        assertEquals(2, result.size());
        assertEquals(2, ((Map<?, ?>) result.get("group1")).size());
    }

    @Test
    public void testListOfPojos() {
        List<SimplePojo> original = Arrays.asList(
                new SimplePojo("Alice", 28, 85000.0),
                new SimplePojo("Bob", 35, 120000.0),
                new SimplePojo("Carol", 22, 55000.0)
        );
        byte[] bytes = Serializer.toBytes(original);
        List result = Serializer.fromBytes(bytes, List.class);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testMapOfPojos() {
        Map<String, SimplePojo> original = new LinkedHashMap<>();
        original.put("alice", new SimplePojo("Alice", 28, 85000.0));
        original.put("bob",   new SimplePojo("Bob",   35, 120000.0));

        byte[] bytes = Serializer.toBytes(original);
        Map result = Serializer.fromBytes(bytes, Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ── Primitive arrays ──────────────────────────────────────────────────

    @Test
    public void testIntArray() {
        int[] original = {1, -2, 0, Integer.MAX_VALUE, Integer.MIN_VALUE};
        int[] result = roundTrip(original, int[].class);
        assertArrayEquals(original, result);
    }

    @Test
    public void testLongArray() {
        long[] original = {Long.MAX_VALUE, Long.MIN_VALUE, 0L, 42L};
        long[] result = roundTrip(original, long[].class);
        assertArrayEquals(original, result);
    }

    @Test
    public void testDoubleArray() {
        double[] original = {3.14, -2.71, 0.0, Double.MAX_VALUE};
        double[] result = roundTrip(original, double[].class);
        assertArrayEquals(original, result, 0.0);
    }

    @Test
    public void testBooleanArray() {
        boolean[] original = {true, false, true, true, false};
        boolean[] result = roundTrip(original, boolean[].class);
        assertArrayEquals(original, result);
    }

    // ── String edge cases ─────────────────────────────────────────────────

    @Test
    public void testEmptyString() {
        String result = roundTrip("", String.class);
        assertEquals("", result);
    }

    @Test
    public void testStringsWithQuotesAndEscapes() {
        String original = "He said \"hello\\world\"\nNew line\tTab";
        String result = roundTrip(original, String.class);
        assertEquals(original, result);
    }

    @Test
    public void testStringWithNullBytes() {
        String original = "before after";
        String result = roundTrip(original, String.class);
        assertEquals(original, result);
    }

    // ── POJO with all-null fields ─────────────────────────────────────────

    @Test
    public void testPojoAllNullFields() {
        SimplePojo original = new SimplePojo(null, 0, 0.0);
        SimplePojo result = roundTrip(original, SimplePojo.class);
        assertNull(result.getName());
        assertEquals(0, result.getAge());
        assertEquals(0.0, result.getSalary(), 0.0);
    }

    // ── Deeply nested POJO ────────────────────────────────────────────────

    @Test
    public void testDeeplyNestedPojo() {
        // Build a chain: pojo1 -> pojo2 -> pojo3 via ComplexPojo nesting
        SimplePojo inner = new SimplePojo("level3", 3, 300.0);
        models.ComplexPojo mid = new models.ComplexPojo(
                Arrays.asList("mid"),
                new HashMap<String, Integer>() {{ put("k", 2); }},
                inner
        );
        // Wrap mid inside another ComplexPojo via Map
        Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put("mid", mid);
        wrapper.put("label", "deep");

        byte[] bytes = Serializer.toBytes(wrapper);
        Map result = Serializer.fromBytes(bytes, Map.class);
        assertNotNull(result);
        assertEquals("deep", result.get("label"));
    }

    // ── Boundary / stress ─────────────────────────────────────────────────

    @Test
    public void testVeryLargeList() {
        List<Long> original = new ArrayList<>(100_000);
        for (long i = 0; i < 100_000; i++) original.add(i);
        byte[] bytes = Serializer.toBytes(original);
        List result = Serializer.fromBytes(bytes, List.class);
        assertEquals(100_000, result.size());
        assertEquals(0L, result.get(0));
        assertEquals(99_999L, result.get(99_999));
    }

    @Test
    public void testVeryLargeMap() {
        Map<String, Integer> original = new LinkedHashMap<>();
        for (int i = 0; i < 10_000; i++) original.put("key_" + i, i);
        byte[] bytes = Serializer.toBytes(original);
        Map result = Serializer.fromBytes(bytes, Map.class);
        assertEquals(10_000, result.size());
        assertEquals(42L, result.get("key_42"));
    }

    @Test
    public void testHighPrecisionBigDecimalList() {
        List<BigDecimal> original = Arrays.asList(
                new BigDecimal("0.1"),
                new BigDecimal("0.2"),
                new BigDecimal("0.30000000000000004"),
                new BigDecimal("1234567890.1234567890")
        );
        byte[] bytes = Serializer.toBytes(original);
        List result = Serializer.fromBytes(bytes, List.class);
        assertEquals(4, result.size());
    }

    // ── Idempotency: serialize twice → same bytes ─────────────────────────

    @Test
    public void testTemporalPojoIdempotent() {
        TemporalPojo pojo = new TemporalPojo(
                LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS), null, null, null,
                Instant.now().truncatedTo(ChronoUnit.MILLIS),
                Duration.ofHours(1), Period.ofWeeks(2)
        );
        byte[] first  = Serializer.toBytes(pojo);
        byte[] second = Serializer.toBytes(pojo);
        assertArrayEquals(first, second);
    }

    @Test
    public void testFinancialRecordIdempotent() {
        FinancialRecord record = new FinancialRecord(
                1L, new BigDecimal("500.00"), new BigDecimal("50.00"),
                BigInteger.TEN, Status.FAILED, "GBP",
                Arrays.asList(new BigDecimal("250.00"), new BigDecimal("250.00")),
                Collections.emptyMap()
        );
        assertArrayEquals(Serializer.toBytes(record), Serializer.toBytes(record));
    }

    // ── helper ────────────────────────────────────────────────────────────

    private static <T> T roundTrip(T value, Class<T> clazz) {
        byte[] bytes = Serializer.toBytes(value);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        return Serializer.fromBytes(bytes, clazz);
    }
}

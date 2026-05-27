package com.tabariyya.synCache;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JavaTimeConverter;
import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.runtime.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.dslplatform.json.StringConverter;
import java.time.*;

public class Serializer {

    private static final DslJson<Object> dslJson;

    static {
        dslJson = new DslJson<>(Settings.withRuntime());

        // Wire up DSL-JSON's built-in java.time converters (not auto-registered in runtime mode)
        dslJson.registerReader(LocalDate.class,     JavaTimeConverter.LOCAL_DATE_READER);
        dslJson.registerWriter(LocalDate.class,     JavaTimeConverter.LOCAL_DATE_WRITER);
        dslJson.registerReader(LocalTime.class,     JavaTimeConverter.LOCAL_TIME_READER);
        dslJson.registerWriter(LocalTime.class,     JavaTimeConverter.LOCAL_TIME_WRITER);
        dslJson.registerReader(LocalDateTime.class, JavaTimeConverter.LOCAL_DATE_TIME_READER);
        dslJson.registerWriter(LocalDateTime.class, JavaTimeConverter.LOCAL_DATE_TIME_WRITER);
        dslJson.registerReader(OffsetTime.class,    JavaTimeConverter.OFFSET_TIME_READER);
        dslJson.registerWriter(OffsetTime.class,    JavaTimeConverter.OFFSET_TIME_WRITER);
        dslJson.registerReader(OffsetDateTime.class,JavaTimeConverter.DATE_TIME_READER);
        dslJson.registerWriter(OffsetDateTime.class,JavaTimeConverter.DATE_TIME_WRITER);
        // ZonedDateTime: use string rather than JavaTimeConverter's writer, which strips the zone ID
        dslJson.registerReader(ZonedDateTime.class, r -> r.wasNull() ? null : ZonedDateTime.parse(StringConverter.deserialize(r)));
        dslJson.registerWriter(ZonedDateTime.class, (w, v) -> { if (v == null) w.writeNull(); else StringConverter.serialize(v.toString(), w); });

        // Instant/Duration absent from JavaTimeConverter — store as epoch millis / total millis
        dslJson.registerReader(Instant.class, r -> r.wasNull() ? null : Instant.ofEpochMilli(NumberConverter.deserializeLong(r)));
        dslJson.registerWriter(Instant.class, (w, v) -> { if (v == null) w.writeNull(); else NumberConverter.serialize(v.toEpochMilli(), w); });
        dslJson.registerReader(Duration.class, r -> r.wasNull() ? null : Duration.ofMillis(NumberConverter.deserializeLong(r)));
        dslJson.registerWriter(Duration.class, (w, v) -> { if (v == null) w.writeNull(); else NumberConverter.serialize(v.toMillis(), w); });

        // Period has no epoch representation — store as ISO-8601 string (e.g. "P1Y2M3D")
        dslJson.registerReader(Period.class, r -> r.wasNull() ? null : Period.parse(StringConverter.deserialize(r)));
        dslJson.registerWriter(Period.class, (w, v) -> { if (v == null) w.writeNull(); else StringConverter.serialize(v.toString(), w); });
    }

    public static <T> byte[] toBytes(T obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            dslJson.serialize(obj, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return dslJson.deserialize(clazz, new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}

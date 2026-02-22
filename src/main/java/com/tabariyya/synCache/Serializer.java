package com.tabariyya.synCache;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Serializer {

    // DSL-JSON is not thread-safe → use ThreadLocal
    private static final ThreadLocal<DslJson<Object>> dslThreadLocal =
            ThreadLocal.withInitial(DslJson::new);
    private static final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().allowArrayFormat(true).includeServiceLoader());

    public static <T> byte[] toBytes(T obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            dslJson.serialize(obj, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("DSL-JSON serialization failed", e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return dslJson.deserialize(clazz, new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("DSL-JSON deserialization failed", e);
        }
    }
}

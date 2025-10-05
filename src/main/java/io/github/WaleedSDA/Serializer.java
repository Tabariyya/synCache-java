package io.github.WaleedSDA;

import com.dslplatform.json.DslJson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Serializer {

    // DSL-JSON is not thread-safe → use ThreadLocal
    private static final ThreadLocal<DslJson<Object>> dslThreadLocal =
            ThreadLocal.withInitial(DslJson::new);

    public static <T> byte[] toBytes(T obj) {
        DslJson<Object> dslJson = dslThreadLocal.get();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            dslJson.serialize(obj, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("DSL-JSON serialization failed", e);
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        DslJson<Object> dslJson = dslThreadLocal.get();
        try {
            return dslJson.deserialize(clazz, new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("DSL-JSON deserialization failed", e);
        }
    }
}

package com.synCache;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller implements AutoCloseable {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }

    private long nativeHandle;
    private final ObjectMapper mapper = new ObjectMapper();

    public Controller(String rabbitMqConnectionUri, long maxNoOfEntries) {
        this.nativeHandle = create(rabbitMqConnectionUri, maxNoOfEntries);
    }

    public void set(CacheEntry entry) {
        set(nativeHandle, entry.getNameSpace(), entry.getId(), entry.getValue(), entry.getTtl());
    }

    /**
     * Returns value string or null if not present
     */
    public <T> T get(String nameSpace, String id, Class<T> clazz) {
        String json = get(nativeHandle, nameSpace, id);

        // mapper.readValue takes a String and a Class<T>
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void evict(String nameSpace, String id) {
        evict(nativeHandle, nameSpace, id);
    }

    @Override
    public void close() {
        long h = nativeHandle;
        if (h != 0) {
            destroy(h);
            nativeHandle = 0;
        }
    }

    // ---- Native declarations ----
    private static native long create(String uri, long maxEntries);

    private static native void destroy(long handle);

    private static native void set(long controllerHandle, String nameSpace, String id, String value, Long ttl);

    private static native String get(long controllerHandle, String nameSpace, String id);

    private static native void evict(long controllerHandle, String nameSpace, String id);
}

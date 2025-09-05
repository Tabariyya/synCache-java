package com.synCache.cache;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NativeController implements AutoCloseable {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }

    private long nativeHandle;
    private final ObjectMapper mapper = new ObjectMapper();

    public NativeController(String rabbitMqConnectionUri, long maxNoOfEntries) {
        this.nativeHandle = nCreate(rabbitMqConnectionUri, maxNoOfEntries);
    }

    public void set(NativeCacheEntry entry) {
        nSet(nativeHandle, entry.handle());
    }

    /**
     * Returns value string or null if not present
     */
    public <T> T get(String nameSpace, String id, Class<T> clazz) {
        String json = nGet(nativeHandle, nameSpace, id);

        // mapper.readValue takes a String and a Class<T>
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void evict(String nameSpace, String id) {
        nEvict(nativeHandle, nameSpace, id);
    }

    @Override
    public void close() {
        long h = nativeHandle;
        if (h != 0) {
            nDestroy(h);
            nativeHandle = 0;
        }
    }

    // ---- Native declarations ----
    private static native long nCreate(String uri, long maxEntries);

    private static native void nDestroy(long handle);

    private static native void nSet(long controllerHandle, long entryHandle);

    private static native String nGet(long controllerHandle, String nameSpace, String id);

    private static native void nEvict(long controllerHandle, String nameSpace, String id);
}

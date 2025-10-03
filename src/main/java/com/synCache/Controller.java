package com.synCache;


public class Controller implements AutoCloseable {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }

    private long nativeHandle;

    public Controller(String rabbitMqConnectionUri, long maxNoOfEntries, boolean async) {
        this.nativeHandle = create(rabbitMqConnectionUri, maxNoOfEntries, async);
    }

    public void set(CacheEntry entry) {
        set(nativeHandle, entry.getNameSpace(), entry.getId(), entry.getValue(), entry.getTtl());
    }


    public <T> T get(String nameSpace, String id, Class<T> clazz) {
        byte[] json = get(nativeHandle, nameSpace, id);
        if (json == null) {
            return null;
        }
        return Serializer.fromBytes(json, clazz);
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
    private static native long create(String uri, long maxEntries, boolean async);

    private static native void destroy(long handle);

    private static native void set(long controllerHandle, String nameSpace, String id, byte[] value, Long ttl);

    private static native byte[] get(long controllerHandle, String nameSpace, String id);

    private static native void evict(long controllerHandle, String nameSpace, String id);
}

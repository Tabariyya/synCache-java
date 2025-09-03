package com.synCache.cache;


public class NativeController implements AutoCloseable {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }

    private long nativeHandle;

    public NativeController(String rabbitMqConnectionUri, long maxNoOfEntries) {
        this.nativeHandle = nCreate(rabbitMqConnectionUri, maxNoOfEntries);
    }

    public void set(NativeCacheEntry entry) {
        nSet(nativeHandle, entry.handle());
    }

    /**
     * Returns value string or null if not present
     */
    public String get(String nameSpace, String id) {
        return nGet(nativeHandle, nameSpace, id);
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

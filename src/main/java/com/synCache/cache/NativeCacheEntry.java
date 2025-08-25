package com.synCache.cache;

public class NativeCacheEntry implements AutoCloseable {

    // Opaque pointer to C++ CacheEntry
    private long nativeHandle;

    public NativeCacheEntry(String id, String nameSpace, String value, Long ttlSecondsOrNull) {
        this.nativeHandle = nCreate(id, nameSpace, value, ttlSecondsOrNull);
    }

    public String getId() {
        return nGetId(nativeHandle);
    }

    public String getNameSpace() {
        return nGetNameSpace(nativeHandle);
    }

    public String getValue() {
        return nGetValue(nativeHandle);
    }

    public Long getTtlOrNull() {
        return nGetTtlOrNull(nativeHandle);
    }

    public int getAccessFrequency() {
        return nGetAccessFrequency(nativeHandle);
    }

    public void increaseAccessFrequency() {
        nIncreaseAccessFrequency(nativeHandle);
    }

    // Deterministic cleanup
    @Override
    public void close() {
        long h = nativeHandle;
        if (h != 0) {
            nDestroy(h);
            nativeHandle = 0;
        }
    }

    long handle() {
        return nativeHandle;
    }

    // ---- Native declarations (static + explicit handle) ----
    private static native long nCreate(String id, String nameSpace, String value, Long ttlSecondsOrNull);

    private static native void nDestroy(long handle);

    private static native String nGetId(long handle);

    private static native String nGetNameSpace(long handle);

    private static native String nGetValue(long handle);

    private static native Long nGetTtlOrNull(long handle);

    private static native int nGetAccessFrequency(long handle);

    private static native void nIncreaseAccessFrequency(long handle);
}

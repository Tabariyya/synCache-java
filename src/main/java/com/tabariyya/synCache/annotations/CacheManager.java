package com.tabariyya.synCache.annotations;

import com.tabariyya.synCache.Cache;

public final class CacheManager {

    private static volatile Cache instance;

    public static void initialize(String token, long maxEntries) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new Cache(token, maxEntries);
                }
            }
        }
    }

    public static Cache getInstance() {
        if (instance == null)
            throw new IllegalStateException("Cache not initialized");
        return instance;
    }
}


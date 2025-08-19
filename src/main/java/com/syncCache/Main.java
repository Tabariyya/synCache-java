package com.syncCache;

import com.syncCache.cache.NativeCacheEntry;
import com.syncCache.cache.NativeController;

public class Main {
    public static void main(String[] args) {
        System.out.println("HEEEY: " + System.getProperty("java.library.path"));

        try (NativeController ctrl = new NativeController("amqp://localhost", 100)) {
            try (NativeCacheEntry e = new NativeCacheEntry("1", "ns", "value", /*ttl*/ null)) {
                ctrl.set(e, null);
            }
            String v = ctrl.get("ns", "1");
            System.out.println("value=" + v);
            ctrl.evict("ns", "1");
        }

    }

}
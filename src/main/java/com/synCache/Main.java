package com.synCache;

import com.synCache.cache.NativeCacheEntry;
import com.synCache.cache.NativeController;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        NativeController ctrl = new NativeController("amqp://guest:guest@localhost:5672/", 100);
        NativeCacheEntry e = new NativeCacheEntry("1", "ns", "value", /*ttl*/ null);
        ctrl.set(e, null);
        System.out.println("sleeping");
        Thread.sleep(10000);
        String v = ctrl.get("ns", "1");
        System.out.println("value=" + v);
        Thread.sleep(10000);

//        ctrl.evict("ns", "1");
    }

}
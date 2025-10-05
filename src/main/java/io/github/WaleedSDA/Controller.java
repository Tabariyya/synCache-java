package io.github.WaleedSDA;


public class Controller {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }


    public Controller(String rabbitMqConnectionUri, long maxNoOfEntries, boolean async) {
        nCreate(rabbitMqConnectionUri, maxNoOfEntries, async);
    }

    public void set(CacheEntry entry) {
        nSet(entry.getNameSpace(), entry.getId(), entry.getValue(), entry.getTtl());
    }


    public <T> T get(String nameSpace, String id, Class<T> clazz) {
        byte[] json = nGet(nameSpace, id);
        if (json == null) {
            return null;
        }
        return Serializer.fromBytes(json, clazz);
    }

    public void evict(String nameSpace, String id) {
        nEvict(nameSpace, id);
    }


    // ---- Native declarations ----
    private native void nCreate(String uri, long maxEntries, boolean async);

    private native void nSet(String nameSpace, String id, byte[] value, Long ttl);

    private native byte[] nGet(String nameSpace, String id);

    private native void nEvict(String nameSpace, String id);
}

package io.github.WaleedSDA.synCache;


public class Controller {
    static {
        System.load(LibraryLoader.getLibraryPath());
    }


    public Controller(String brokerUrl, String BrokerToken, long maxNoOfEntries) {
        nCreate(brokerUrl, BrokerToken, maxNoOfEntries);
    }

    public void set(String nameSpace, String id, Object value, Long ttl) {
        nSet(nameSpace, id, Serializer.toBytes(value), ttl);
    }

    public void set(String nameSpace, String id, Object value) {
        nSet(nameSpace, id, Serializer.toBytes(value), null);
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
    private native void nCreate(String uri, String BrokerToken, long maxEntries);

    private native void nSet(String nameSpace, String id, byte[] value, Long ttl);

    private native byte[] nGet(String nameSpace, String id);

    private native void nEvict(String nameSpace, String id);
}

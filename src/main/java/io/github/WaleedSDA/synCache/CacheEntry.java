package io.github.WaleedSDA.synCache;


public class CacheEntry {

    private final String nameSpace;
    private final String id;
    private final byte[] value;
    private final Long ttl;

    public CacheEntry(String nameSpace, String id, Object value, long ttl) {
        this.nameSpace = nameSpace;
        this.id = id;
        this.value = Serializer.toBytes(value);
        this.ttl = ttl;
    }

    public CacheEntry(String nameSpace, String id, Object value) {
        this.nameSpace = nameSpace;
        this.id = id;
        this.value = Serializer.toBytes(value);
        this.ttl = null;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getId() {
        return id;
    }

    public byte[] getValue() {
        return value;
    }

    public Long getTtl() {
        return ttl;
    }
}

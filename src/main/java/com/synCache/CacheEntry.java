package com.synCache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CacheEntry<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final String nameSpace;
    private final String id;
    private final String value;
    private final Long ttl;

    public CacheEntry(String nameSpace, String id, T value, Long ttl) {
        this.nameSpace = nameSpace;
        this.id = id;
        try {
            this.value = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.ttl = ttl;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Long getTtl() {
        return ttl;
    }
}

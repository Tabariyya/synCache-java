package services;

import com.tabariyya.myplugin.TimedExecution;
import com.tabariyya.synCache.aop.annotations.CacheEvict;
import com.tabariyya.synCache.aop.annotations.Cacheable;

public class TestService {

    @Cacheable(namespace = "users", key = "#id")
    @TimedExecution("fetchUser")
    public int loadUser(int id) {
        System.out.println("loadUser " + id);
        return id;
    }

    @CacheEvict(namespace = "users", key = "#id")
    public void evictUser(int id) {
    }

    @CacheEvict(namespace = "users", allEntries = true)
    public void evictAll() {
    }

    @CacheEvict(namespace = "users", key = "#id")
    public int evictAndReturn(int id) {
        return id;
    }

    @CacheEvict(namespace = "users", key = "#result")
    public int evictByResult(int id) {
        return id;
    }

}

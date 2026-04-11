package com.tabariyya.synCache.aop.interceptors;

import com.tabariyya.synCache.Cache;
import com.tabariyya.synCache.aop.CacheManager;
import com.tabariyya.synCache.aop.KeyExpressionEvaluator;
import com.tabariyya.synCache.aop.annotations.CacheEvict;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CacheEvictInterceptor {

    @RuntimeType
    public static Object intercept(
            @Origin Method method,
            @SuperCall Callable<?> superCall,
            @AllArguments Object[] args
    ) throws Exception {

        // Always execute the method first, then evict
        Object result = superCall.call();

        CacheEvict annotation = method.getAnnotation(CacheEvict.class);
        Cache cache = CacheManager.getInstance();

        if (annotation.allEntries()) {
            cache.evict(annotation.namespace());
            System.out.printf("[SynCache] CacheEvict ALL — %s%n",
                    annotation.namespace());
        } else {
            String key = (String) KeyExpressionEvaluator.eval(
                    annotation.key(), method, args, result
            );
            cache.evict(annotation.namespace(), key);
            System.out.printf("[SynCache] CacheEvict — %s :: %s%n",
                    annotation.namespace(), key);
        }

        return result;
    }
}
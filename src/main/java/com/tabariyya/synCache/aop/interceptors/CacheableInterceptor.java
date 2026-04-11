package com.tabariyya.synCache.aop.interceptors;


import com.tabariyya.synCache.Cache;
import com.tabariyya.synCache.aop.CacheManager;
import com.tabariyya.synCache.aop.KeyExpressionEvaluator;
import com.tabariyya.synCache.aop.annotations.Cacheable;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CacheableInterceptor {

    @RuntimeType
    public static Object intercept(
            @Origin Method method,
            @SuperCall Callable<?> superCall,
            @AllArguments Object[] args
    ) throws Exception {

        // void methods bypass caching entirely
        if (method.getReturnType() == Void.TYPE) {
            return superCall.call();
        }

        Cacheable annotation = method.getAnnotation(Cacheable.class);

        String key = (String) KeyExpressionEvaluator.eval(
                annotation.key(), method, args, null
        );

        Cache cache = CacheManager.getInstance();
        Object cached = cache.get(annotation.namespace(), key, method.getReturnType());

        if (cached != null) {
            System.out.printf("[SynCache] Cache HIT  — %s :: %s%n",
                    annotation.namespace(), key);
            return cached;
        }

        Object result = superCall.call();

        if (result != null) {
            cache.set(annotation.namespace(), key, result);
            System.out.printf("[SynCache] Cache MISS — %s :: %s (stored)%n",
                    annotation.namespace(), key);
        }

        return result;
    }
}
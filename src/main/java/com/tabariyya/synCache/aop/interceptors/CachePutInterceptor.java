package com.tabariyya.synCache.aop.interceptors;


import com.tabariyya.synCache.aop.CacheManager;
import com.tabariyya.synCache.aop.KeyExpressionEvaluator;
import com.tabariyya.synCache.aop.annotations.CachePut;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CachePutInterceptor {

    @RuntimeType
    public static Object intercept(
            @Origin Method method,
            @SuperCall Callable<?> superCall,
            @AllArguments Object[] args
    ) throws Exception {

        // Always execute the method first
        Object result = superCall.call();

        CachePut annotation = method.getAnnotation(CachePut.class);

        String key = (String) KeyExpressionEvaluator.eval(
                annotation.key(), method, args, result
        );

        CacheManager.getInstance().set(annotation.namespace(), key, result);

        System.out.printf("[SynCache] CachePut — %s :: %s%n",
                annotation.namespace(), key);

        return result;
    }
}

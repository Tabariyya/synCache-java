package com.tabariyya.synCache.annotations;

import com.tabariyya.synCache.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class CacheAspect {

    @Around("@annotation(cacheable)")
    public Object around(ProceedingJoinPoint pjp, Cacheable cacheable) throws Throwable {
        System.out.println("We entered cacheable");

        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();

        Class<?> returnType = sig.getReturnType();
        if (returnType == Void.TYPE) {
            return pjp.proceed();
        }

        Object[] args = pjp.getArgs();

        String key = (String) KeyExpressionEvaluator.eval(
                cacheable.key(),
                method,
                args,
                null
        );

        Cache cache = CacheManager.getInstance();

        Object cached = cache.get(cacheable.namespace(), key, returnType);
        if (cached != null) {
            return cached;
        }

        Object result = pjp.proceed();

        if (result != null) {
            cache.set(cacheable.namespace(), key, result);
        }

        return result;
    }


    @Around("@annotation(cachePut)")
    public Object cachePut(ProceedingJoinPoint pjp, CachePut cachePut) throws Throwable {

        Method method = ((org.aspectj.lang.reflect.MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();

        Object result = pjp.proceed();

        String key = (String) KeyExpressionEvaluator.eval(
                cachePut.key(), method, args, result);

        CacheManager.getInstance().set(cachePut.namespace(), key, result);
        return result;
    }

    @Around("@annotation(cacheEvict)")
    public Object cacheEvict(ProceedingJoinPoint pjp, CacheEvict cacheEvict) throws Throwable {

        Method method = ((org.aspectj.lang.reflect.MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();

        Object result = pjp.proceed();

        Cache cache = CacheManager.getInstance();

        if (cacheEvict.allEntries()) {
            cache.evict(cacheEvict.namespace());
        } else {
            String key = (String) KeyExpressionEvaluator.eval(
                    cacheEvict.key(), method, args, result);
            cache.evict(cacheEvict.namespace(), key);
        }

        return result;
    }
}

package com.tabariyya.synCache.aop.interceptors;

import com.tabariyya.synCache.aop.CacheManager;
import com.tabariyya.synCache.aop.KeyExpressionEvaluator;
import com.tabariyya.synCache.aop.annotations.Cacheable;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

public class CacheableInterceptor {

    // Stores the cached value if found — null means proceed normally
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static Object onEnter(@Advice.Origin Method method,
                          @Advice.AllArguments Object[] args) {

        if (method.getReturnType() == Void.TYPE) return null;

        Cacheable annotation = method.getAnnotation(Cacheable.class);
        if (annotation == null) return null;

        try {
            String key = (String) KeyExpressionEvaluator.eval(
                    annotation.key(), method, args, null);

            Object cached = CacheManager.getInstance()
                    .get(annotation.namespace(), key, method.getReturnType());

            if (cached != null) {
                System.out.printf("[SynCache] Cache HIT — %s :: %s%n",
                        annotation.namespace(), key);
                return cached; // non-null → skipOn triggers → method body skipped
            }
        } catch (Exception e) {
            System.err.println("[SynCache] Cacheable enter error: " + e.getMessage());
        }

        return null; // null → method body executes normally
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    static void onExit(@Advice.Origin Method method,
                       @Advice.AllArguments Object[] args,
                       @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object result,
                       @Advice.Enter Object enterResult) {

        // If enterResult was non-null, we skipped the method — return the cached value
        if (enterResult != null) {
            result = enterResult;
            return;
        }

        if (result == null) return;

        Cacheable annotation = method.getAnnotation(Cacheable.class);
        if (annotation == null) return;

        try {
            String key = (String) KeyExpressionEvaluator.eval(
                    annotation.key(), method, args, null);

            CacheManager.getInstance().set(annotation.namespace(), key, result);
            System.out.printf("[SynCache] Cache MISS — %s :: %s (stored)%n",
                    annotation.namespace(), key);
        } catch (Exception e) {
            System.err.println("[SynCache] Cacheable exit error: " + e.getMessage());
        }
    }
}
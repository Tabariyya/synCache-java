package com.tabariyya.synCache.aop.agent;

import com.tabariyya.synCache.aop.annotations.CacheEvict;
import com.tabariyya.synCache.aop.annotations.CachePut;
import com.tabariyya.synCache.aop.annotations.Cacheable;
import com.tabariyya.synCache.aop.interceptors.CacheEvictInterceptor;
import com.tabariyya.synCache.aop.interceptors.CachePutInterceptor;
import com.tabariyya.synCache.aop.interceptors.CacheableInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public final class CacheAgent {

    // Called when attached via -javaagent: CLI flag (optional, still works)
    public static void premain(String args, Instrumentation inst) {
        installTransformer(inst);
    }

    // Called when attached dynamically at runtime (our path)
    public static void agentmain(String args, Instrumentation inst) {
        installTransformer(inst);
    }

    static void installTransformer(Instrumentation inst) {
        new AgentBuilder.Default(new ByteBuddy())
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module, domain) ->
                        builder
                                .visit(Advice.to(CacheableInterceptor.class)
                                        .on(ElementMatchers.isAnnotatedWith(Cacheable.class)))
                )
                .installOn(inst);
    }
}
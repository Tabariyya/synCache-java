package com.tabariyya.synCache.aop.agent;

import net.bytebuddy.agent.ByteBuddyAgent;

public final class AgentLoader {

    private static volatile boolean loaded = false;

    static {
        install();
    }

    public static synchronized void install() {
        if (loaded) return;
        try {
            ByteBuddyAgent.install();
            CacheAgent.installTransformer(ByteBuddyAgent.getInstrumentation());
            loaded = true;
        } catch (Exception e) {
            System.err.println("[TimedExecution] Failed to self-attach agent: " + e.getMessage());
        }
    }

    private AgentLoader() {}
}
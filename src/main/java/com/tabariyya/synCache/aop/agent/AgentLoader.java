package com.tabariyya.synCache.aop.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import java.lang.instrument.Instrumentation;

public final class AgentLoader {

    private static volatile boolean loaded = false;

    static { install(); }

    public static synchronized void install() {
        if (loaded) return;
        try {
            Instrumentation inst = getOrAttach();
            CacheAgent.installTransformer(inst);
            loaded = true;
        } catch (Exception e) {
            System.err.println("[SynCache] Failed to self-attach agent: " + e.getMessage());
        }
    }

    private static Instrumentation getOrAttach() {
        try {
            // If any agent is already present this succeeds immediately
            return ByteBuddyAgent.getInstrumentation();
        } catch (IllegalStateException ignored) {
            // No agent yet — safe to self-attach
            System.setProperty("jdk.attach.allowAttachSelf", "true");
            ByteBuddyAgent.install();
            return ByteBuddyAgent.getInstrumentation();
        }
    }

    private AgentLoader() {}
}
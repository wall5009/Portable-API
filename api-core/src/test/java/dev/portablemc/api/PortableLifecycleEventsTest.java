/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for lifecycle callback dispatch.
 */
final class PortableLifecycleEventsTest {
    @Test
    void firesRegisteredCallbacks() {
        PortableLifecycleEvents lifecycle = new PortableLifecycleEvents();
        AtomicInteger fired = new AtomicInteger();

        lifecycle.onCommonSetup(fired::incrementAndGet);
        lifecycle.onClientSetup(fired::incrementAndGet);
        lifecycle.onServerStarting(server -> fired.addAndGet(server.worldDirectory().endsWith("world") ? 1 : 0));

        lifecycle.fireCommonSetup();
        lifecycle.fireClientSetup();
        lifecycle.fireServerStarting(new PortableServerContext(Path.of("world")));

        assertEquals(3, fired.get());
    }
}

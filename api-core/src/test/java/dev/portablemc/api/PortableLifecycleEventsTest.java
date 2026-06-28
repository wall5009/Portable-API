/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

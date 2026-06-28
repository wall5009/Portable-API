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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Lifecycle callback registry shared by all supported loaders.
 *
 * <p>Only events with a stable enough equivalent across the target matrix are
 * exposed. Fabric has no Forge-style "common setup" event, so Fabric adapters
 * fire common setup immediately after the shared initializer returns. Code that
 * depends on exact ordering against another loader-specific event should remain
 * in a loader module.</p>
 */
public final class PortableLifecycleEvents {
    private final List<Runnable> commonSetupCallbacks = new CopyOnWriteArrayList<>();
    private final List<Runnable> clientSetupCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableServerContext>> serverStartingCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableDataGenerationContext>> dataGenerationCallbacks = new CopyOnWriteArrayList<>();

    /**
     * Registers work that should run after basic mod construction and content
     * registration. Keep this callback short; long work should be moved to a
     * later game event or a data-generation task.
     *
     * @param callback setup work
     */
    public void onCommonSetup(final Runnable callback) {
        commonSetupCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers client-only setup. The callback is never fired on dedicated
     * server processes, and shared code must still avoid referencing client-only
     * Minecraft classes directly.
     *
     * @param callback client setup work
     */
    public void onClientSetup(final Runnable callback) {
        clientSetupCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers work for the dedicated/integrated server-starting event.
     *
     * @param callback server-starting callback
     */
    public void onServerStarting(final Consumer<PortableServerContext> callback) {
        serverStartingCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers build-time data/resource generation work.
     *
     * @param callback data-generation callback
     */
    public void onDataGeneration(final Consumer<PortableDataGenerationContext> callback) {
        dataGenerationCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Fires common setup callbacks. Loader adapters call this from their
     * platform-specific lifecycle bridge.
     */
    public void fireCommonSetup() {
        commonSetupCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires client setup callbacks.
     */
    public void fireClientSetup() {
        clientSetupCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires server-starting callbacks.
     *
     * @param context server context
     */
    public void fireServerStarting(final PortableServerContext context) {
        serverStartingCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires data-generation callbacks.
     *
     * @param context data-generation context
     */
    public void fireDataGeneration(final PortableDataGenerationContext context) {
        dataGenerationCallbacks.forEach(callback -> callback.accept(context));
    }
}

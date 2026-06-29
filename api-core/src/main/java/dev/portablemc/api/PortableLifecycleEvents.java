/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final List<Consumer<PortableServerContext>> serverStartedCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableServerContext>> serverStoppingCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableServerContext>> serverStoppedCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableServerContext>> serverTickCallbacks = new CopyOnWriteArrayList<>();
    private final List<Runnable> clientTickCallbacks = new CopyOnWriteArrayList<>();
    private final List<Runnable> reloadCallbacks = new CopyOnWriteArrayList<>();
    private final List<Consumer<PortableDataGenerationContext>> dataGenerationCallbacks = new CopyOnWriteArrayList<>();
    private final AtomicBoolean commonSetupFired = new AtomicBoolean();
    private final AtomicBoolean clientSetupFired = new AtomicBoolean();
    private ServerState serverState = ServerState.STOPPED;

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
     * Registers work for the server-started event. It fires after the server is
     * considered running and before regular server tick callbacks.
     *
     * @param callback server-started callback
     */
    public void onServerStarted(final Consumer<PortableServerContext> callback) {
        serverStartedCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers work for the server-stopping event.
     *
     * @param callback server-stopping callback
     */
    public void onServerStopping(final Consumer<PortableServerContext> callback) {
        serverStoppingCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers work for the server-stopped event.
     *
     * @param callback server-stopped callback
     */
    public void onServerStopped(final Consumer<PortableServerContext> callback) {
        serverStoppedCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers end-of-server-tick work. Loader adapters call this only from a
     * stable server tick equivalent on the game thread.
     *
     * @param callback server-tick callback
     */
    public void onServerTick(final Consumer<PortableServerContext> callback) {
        serverTickCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers end-of-client-tick work. This callback is never fired on a
     * dedicated server process.
     *
     * @param callback client-tick callback
     */
    public void onClientTick(final Runnable callback) {
        clientTickCallbacks.add(Objects.requireNonNull(callback, "callback"));
    }

    /**
     * Registers generic reload work after platform reload listeners complete.
     *
     * @param callback reload callback
     */
    public void onReload(final Runnable callback) {
        reloadCallbacks.add(Objects.requireNonNull(callback, "callback"));
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
        requireFirstFire(commonSetupFired, "common setup");
        commonSetupCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires client setup callbacks.
     */
    public void fireClientSetup() {
        requireFirstFire(clientSetupFired, "client setup");
        clientSetupCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires server-starting callbacks.
     *
     * @param context server context
     */
    public void fireServerStarting(final PortableServerContext context) {
        synchronized (this) {
            if (serverState != ServerState.STOPPED) {
                throw new IllegalStateException("Cannot fire server starting while server state is " + serverState);
            }
            serverState = ServerState.STARTING;
        }
        serverStartingCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires server-started callbacks.
     *
     * @param context server context
     */
    public void fireServerStarted(final PortableServerContext context) {
        synchronized (this) {
            if (serverState != ServerState.STARTING) {
                throw new IllegalStateException("Cannot fire server started while server state is " + serverState);
            }
            serverState = ServerState.STARTED;
        }
        serverStartedCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires server-stopping callbacks.
     *
     * @param context server context
     */
    public void fireServerStopping(final PortableServerContext context) {
        synchronized (this) {
            if (serverState != ServerState.STARTED) {
                throw new IllegalStateException("Cannot fire server stopping while server state is " + serverState);
            }
            serverState = ServerState.STOPPING;
        }
        serverStoppingCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires server-stopped callbacks and allows a later server instance to
     * begin a new start cycle in the same physical client process.
     *
     * @param context server context
     */
    public void fireServerStopped(final PortableServerContext context) {
        synchronized (this) {
            if (serverState != ServerState.STOPPING) {
                throw new IllegalStateException("Cannot fire server stopped while server state is " + serverState);
            }
            serverState = ServerState.STOPPED;
        }
        serverStoppedCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires server tick callbacks.
     *
     * @param context server context
     */
    public void fireServerTick(final PortableServerContext context) {
        synchronized (this) {
            if (serverState != ServerState.STARTED) {
                return;
            }
        }
        serverTickCallbacks.forEach(callback -> callback.accept(context));
    }

    /**
     * Fires client tick callbacks.
     */
    public void fireClientTick() {
        clientTickCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires reload callbacks.
     */
    public void fireReload() {
        reloadCallbacks.forEach(Runnable::run);
    }

    /**
     * Fires data-generation callbacks.
     *
     * @param context data-generation context
     */
    public void fireDataGeneration(final PortableDataGenerationContext context) {
        dataGenerationCallbacks.forEach(callback -> callback.accept(context));
    }

    private static void requireFirstFire(final AtomicBoolean fired, final String eventName) {
        if (!fired.compareAndSet(false, true)) {
            throw new IllegalStateException("Portable lifecycle event fired more than once: " + eventName);
        }
    }

    private enum ServerState {
        STOPPED,
        STARTING,
        STARTED,
        STOPPING
    }
}

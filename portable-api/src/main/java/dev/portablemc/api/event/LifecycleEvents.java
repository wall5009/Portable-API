package dev.portablemc.api.event;

/** Common lifecycle hooks exposed by Portable API v1. */
public interface LifecycleEvents {
  /** Registers a callback for common setup/bootstrap completion. */
  EventHandle onCommonSetup(Runnable callback);

  /** Registers a callback for client setup. */
  EventHandle onClientSetup(Runnable callback);

  /** Registers a callback for dedicated or integrated server startup. */
  EventHandle onServerStarting(Runnable callback);

  /** Registers a callback for server started. */
  EventHandle onServerStarted(Runnable callback);

  /** Registers a callback for server stopping. */
  EventHandle onServerStopping(Runnable callback);
}

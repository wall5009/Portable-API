package dev.portablemc.api.runtime;

/** Helper for running code only on an appropriate physical side. */
public interface SidedExecutor {
  /** Runs the action only on a physical client. */
  void runWhenClient(Runnable action);

  /** Runs the action only on a dedicated server. */
  void runWhenDedicatedServer(Runnable action);

  /** Returns the active physical side. */
  PhysicalSide physicalSide();
}

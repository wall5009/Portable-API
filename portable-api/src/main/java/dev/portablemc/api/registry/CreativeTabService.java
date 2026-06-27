package dev.portablemc.api.registry;

/** Loader-neutral creative tab population service. */
public interface CreativeTabService {
  /**
   * Adds an item-like registered value to a creative tab when the target supports the registry
   * type.
   */
  EventHandleLike add(Identifier tabId, RegistrySupplier<?> entry);

  /** Minimal unregister handle used without exposing event internals in this package. */
  @FunctionalInterface
  interface EventHandleLike {
    /** Removes the pending population callback when supported. */
    void unregister();
  }
}

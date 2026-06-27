package dev.portablemc.api.event;

/** Handle returned from event registration. */
@FunctionalInterface
public interface EventHandle {
  /** Removes the registered callback when supported by the target. */
  void unregister();

  /** No-op event handle. */
  static EventHandle noop() {
    return () -> {};
  }
}

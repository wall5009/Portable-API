package dev.portablemc.api.registry;

import java.util.function.Supplier;

/** Supplier for an object registered through a deferred registry. */
public interface RegistrySupplier<T> extends Supplier<T> {
  /** Returns the typed key for this registration. */
  RegistryKey<T> key();

  /** Returns whether the target has bound the registered value. */
  boolean isBound();
}

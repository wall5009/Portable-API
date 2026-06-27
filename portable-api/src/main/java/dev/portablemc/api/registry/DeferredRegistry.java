package dev.portablemc.api.registry;

import java.util.function.Supplier;

/** Loader-neutral deferred registration group for a single registry id. */
public interface DeferredRegistry<T> {
  /** Returns the backing registry id. */
  Identifier registryId();

  /** Returns the expected raw value type. */
  Class<T> valueType();

  /** Registers a value supplier. */
  RegistrySupplier<T> register(Identifier id, Supplier<? extends T> supplier);

  /** Freezes this registry and submits pending registrations to the active target. */
  void bind();
}

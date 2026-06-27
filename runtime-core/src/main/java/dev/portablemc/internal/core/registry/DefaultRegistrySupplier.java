package dev.portablemc.internal.core.registry;

import dev.portablemc.api.registry.RegistryKey;
import dev.portablemc.api.registry.RegistrySupplier;
import java.util.Objects;
import java.util.function.Supplier;

/** Default deferred registry supplier. */
final class DefaultRegistrySupplier<T> implements RegistrySupplier<T> {
  private final RegistryKey<T> key;
  private final Supplier<? extends T> factory;
  private volatile T value;

  DefaultRegistrySupplier(RegistryKey<T> key, Supplier<? extends T> factory) {
    this.key = Objects.requireNonNull(key, "key");
    this.factory = Objects.requireNonNull(factory, "factory");
  }

  @Override
  public RegistryKey<T> key() {
    return key;
  }

  @Override
  public boolean isBound() {
    return value != null;
  }

  @Override
  public T get() {
    T current = value;
    if (current == null) {
      throw new IllegalStateException("Registry value is not bound yet: " + key.valueId());
    }
    return current;
  }

  T createAndBind() {
    if (value == null) {
      value = Objects.requireNonNull(factory.get(), "registry factory returned null");
    }
    return value;
  }
}

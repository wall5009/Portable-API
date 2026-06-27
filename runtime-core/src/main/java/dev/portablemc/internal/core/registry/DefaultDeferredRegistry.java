package dev.portablemc.internal.core.registry;

import dev.portablemc.api.registry.DeferredRegistry;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistryKey;
import dev.portablemc.api.registry.RegistrySupplier;
import dev.portablemc.internal.core.TargetBridge;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/** Default deferred registry implementation. */
final class DefaultDeferredRegistry<T> implements DeferredRegistry<T> {
  private final Identifier registryId;
  private final Class<T> valueType;
  private final TargetBridge targetBridge;
  private final List<DefaultRegistrySupplier<T>> entries = new ArrayList<>();
  private boolean bound;

  DefaultDeferredRegistry(Identifier registryId, Class<T> valueType, TargetBridge targetBridge) {
    this.registryId = Objects.requireNonNull(registryId, "registryId");
    this.valueType = Objects.requireNonNull(valueType, "valueType");
    this.targetBridge = Objects.requireNonNull(targetBridge, "targetBridge");
  }

  @Override
  public Identifier registryId() {
    return registryId;
  }

  @Override
  public Class<T> valueType() {
    return valueType;
  }

  @Override
  public synchronized RegistrySupplier<T> register(Identifier id, Supplier<? extends T> supplier) {
    if (bound) {
      throw new IllegalStateException("Deferred registry is already bound: " + registryId);
    }
    DefaultRegistrySupplier<T> registrySupplier =
        new DefaultRegistrySupplier<>(RegistryKey.of(registryId, id, valueType), supplier);
    entries.add(registrySupplier);
    return registrySupplier;
  }

  @Override
  public synchronized void bind() {
    if (bound) {
      return;
    }
    for (DefaultRegistrySupplier<T> entry : entries) {
      T value = entry.createAndBind();
      targetBridge.register(entry.key(), value);
    }
    bound = true;
  }
}

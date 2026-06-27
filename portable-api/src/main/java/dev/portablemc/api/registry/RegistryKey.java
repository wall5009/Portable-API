package dev.portablemc.api.registry;

import java.util.Objects;

/** Typed key for an entry in a registry. */
public record RegistryKey<T>(Identifier registryId, Identifier valueId, Class<T> valueType) {
  public RegistryKey {
    Objects.requireNonNull(registryId, "registryId");
    Objects.requireNonNull(valueId, "valueId");
    Objects.requireNonNull(valueType, "valueType");
  }

  /** Creates a typed registry key. */
  public static <T> RegistryKey<T> of(
      Identifier registryId, Identifier valueId, Class<T> valueType) {
    return new RegistryKey<>(registryId, valueId, valueType);
  }
}

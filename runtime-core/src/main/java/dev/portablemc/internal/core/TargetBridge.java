package dev.portablemc.internal.core;

import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistryKey;

/** Versioned Minecraft bridge implemented by common modules. */
public interface TargetBridge {
  /**
   * Converts a portable identifier to the native Minecraft identifier object for
   * diagnostics/adapters.
   */
  Object toNativeIdentifier(Identifier id);

  /**
   * Binds a raw registry value. Implementations may throw if the registry cannot be safely written.
   */
  <T> void register(RegistryKey<T> key, T value);
}

package dev.portablemc.internal.common.mc1201;

import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistryKey;
import dev.portablemc.internal.core.TargetBridge;
import net.minecraft.resources.ResourceLocation;

/** Minecraft 1.20.1 native conversion bridge. */
public final class Minecraft1201TargetBridge implements TargetBridge {
  @Override
  public Object toNativeIdentifier(Identifier id) {
    return new ResourceLocation(id.namespace(), id.path());
  }

  @Override
  public <T> void register(RegistryKey<T> key, T value) {
    throw new UnsupportedOperationException(
        "Portable API v1 does not perform raw Minecraft 1.20.1 registry writes. "
            + "Use registry suppliers for stable handles or wait for a typed registry adapter.");
  }
}

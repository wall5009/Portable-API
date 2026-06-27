package dev.portablemc.internal.core;

import dev.portablemc.api.runtime.LoaderKind;
import dev.portablemc.api.runtime.PhysicalSide;
import dev.portablemc.api.runtime.PlatformInfo;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Immutable platform info implementation. */
public record DefaultPlatformInfo(
    LoaderKind loader,
    String minecraftVersion,
    Optional<String> loaderVersion,
    PhysicalSide physicalSide,
    boolean production,
    Set<String> loadedMods)
    implements PlatformInfo {
  public DefaultPlatformInfo {
    Objects.requireNonNull(loader, "loader");
    Objects.requireNonNull(minecraftVersion, "minecraftVersion");
    loaderVersion = Objects.requireNonNull(loaderVersion, "loaderVersion");
    Objects.requireNonNull(physicalSide, "physicalSide");
    loadedMods = Set.copyOf(loadedMods);
  }

  @Override
  public boolean isModLoaded(String modId) {
    return loadedMods.contains(modId);
  }
}

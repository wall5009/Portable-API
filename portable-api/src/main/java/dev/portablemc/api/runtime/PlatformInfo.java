package dev.portablemc.api.runtime;

import java.util.Optional;

/** Immutable identity for the active Minecraft target and loader environment. */
public interface PlatformInfo {
  /** Returns the active loader kind. */
  LoaderKind loader();

  /** Returns the Minecraft version string, for example {@code 1.21.1}. */
  String minecraftVersion();

  /** Returns the loader implementation version when known. */
  Optional<String> loaderVersion();

  /** Returns the physical distribution side. */
  PhysicalSide physicalSide();

  /** Returns whether this runtime is a production game runtime instead of a development launch. */
  boolean production();

  /** Returns whether a mod with the supplied id is loaded. */
  boolean isModLoaded(String modId);
}

package dev.portablemc.api.resource;

/** Resource reload callback. */
@FunctionalInterface
public interface ResourceReloadListener {
  /** Called when the owning resource domain reloads. */
  void reload(ResourceAccess resources);
}

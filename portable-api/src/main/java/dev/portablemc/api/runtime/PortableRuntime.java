package dev.portablemc.api.runtime;

import dev.portablemc.api.bootstrap.PortableInitializer;

/** Installed Portable API runtime for one loader and Minecraft target. */
public interface PortableRuntime {
  /** Returns platform identity and environment information. */
  PlatformInfo platform();

  /** Returns the runtime capability report. */
  CapabilityReport capabilities();

  /** Invokes a consumer initializer with a mod-scoped context. */
  void initialize(String modId, PortableInitializer initializer);
}

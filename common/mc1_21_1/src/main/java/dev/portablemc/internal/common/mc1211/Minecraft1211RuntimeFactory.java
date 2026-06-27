package dev.portablemc.internal.common.mc1211;

import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.api.runtime.PlatformInfo;
import dev.portablemc.internal.core.CorePortableRuntime;
import dev.portablemc.internal.core.DefaultCapabilityReport;
import java.util.Objects;

/** Runtime factory for Minecraft 1.21.1 targets. */
public final class Minecraft1211RuntimeFactory {
  private Minecraft1211RuntimeFactory() {}

  /** Creates the shared runtime for a 1.21.1 loader target. */
  public static CorePortableRuntime create(PlatformInfo platform) {
    Objects.requireNonNull(platform, "platform");
    return new CorePortableRuntime(platform, capabilities(), new Minecraft1211TargetBridge());
  }

  /** Returns the 1.21.1 v1 capability profile. */
  public static CapabilityReport capabilities() {
    return DefaultCapabilityReport.builder()
        .supported(Capability.LIFECYCLE_EVENTS)
        .supported(Capability.LOGGING)
        .supported(Capability.ENVIRONMENT)
        .supported(Capability.SIDED_EXECUTION)
        .supported(Capability.CONFIGURATION)
        .supported(Capability.COMMANDS)
        .limited(
            Capability.REGISTRIES,
            "Raw registry writes are available only through target adapters that opt in.")
        .supported(Capability.REGISTRY_SUPPLIERS)
        .limited(
            Capability.CREATIVE_TAB_POPULATION,
            "Requests are stored for loader adapters; item abstractions are deferred.")
        .limited(
            Capability.RESOURCES,
            "Reload listener declarations are stored; full native reload wiring is targeted for v1.1.")
        .limited(
            Capability.NETWORKING, "Packet declarations are stable; send helpers are deferred.")
        .deferred(
            Capability.DATA_ATTACHMENTS,
            "Persistent component storage is deferred to avoid an unstable v1 contract.")
        .supported(Capability.CLIENT_INITIALIZATION)
        .supported(Capability.SERVER_INITIALIZATION)
        .limited(
            Capability.RAW_MINECRAFT_ACCESS, "Only identifier conversion is exposed internally.")
        .deferred(Capability.WORLDGEN, "World generation needs a dedicated abstraction.")
        .deferred(Capability.SCREEN_REGISTRATION, "Client UI registration is not part of v1.")
        .deferred(Capability.RENDERING, "Rendering APIs remain loader and version specific.")
        .deferred(
            Capability.MIXIN_OR_TRANSFORMER_BRIDGES,
            "Bytecode transformation is intentionally outside v1.")
        .build();
  }
}

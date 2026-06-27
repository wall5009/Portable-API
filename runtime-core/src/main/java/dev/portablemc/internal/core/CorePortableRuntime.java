package dev.portablemc.internal.core;

import dev.portablemc.api.bootstrap.ModContext;
import dev.portablemc.api.bootstrap.PortableInitializer;
import dev.portablemc.api.command.CommandSpec;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.api.runtime.PlatformInfo;
import dev.portablemc.api.runtime.PortableRuntime;
import dev.portablemc.internal.core.event.DefaultLifecycleEvents;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/** Default runtime shared by all v1 platform modules. */
public final class CorePortableRuntime implements PortableRuntime {
  private final PlatformInfo platform;
  private final CapabilityReport capabilities;
  private final TargetBridge targetBridge;
  private final DefaultLifecycleEvents lifecycleEvents = new DefaultLifecycleEvents();
  private final List<CoreModContext> contexts = new CopyOnWriteArrayList<>();

  public CorePortableRuntime(
      PlatformInfo platform, CapabilityReport capabilities, TargetBridge targetBridge) {
    this.platform = Objects.requireNonNull(platform, "platform");
    this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
    this.targetBridge = Objects.requireNonNull(targetBridge, "targetBridge");
  }

  @Override
  public PlatformInfo platform() {
    return platform;
  }

  @Override
  public CapabilityReport capabilities() {
    return capabilities;
  }

  @Override
  public void initialize(String modId, PortableInitializer initializer) {
    Objects.requireNonNull(modId, "modId");
    Objects.requireNonNull(initializer, "initializer");
    CoreModContext context = new CoreModContext(modId, this, lifecycleEvents, targetBridge);
    contexts.add(context);
    initializer.initialize(context);
  }

  /** Returns all command declarations registered by initialized mods. */
  public Stream<CommandSpec> commandSpecs() {
    return contexts.stream().flatMap(context -> context.commandService().commands().stream());
  }

  /** Fires the common setup lifecycle phase. */
  public void fireCommonSetup() {
    lifecycleEvents.fireCommonSetup();
  }

  /** Fires the client setup lifecycle phase. */
  public void fireClientSetup() {
    lifecycleEvents.fireClientSetup();
  }

  /** Fires the server starting lifecycle phase. */
  public void fireServerStarting() {
    lifecycleEvents.fireServerStarting();
  }

  /** Fires the server started lifecycle phase. */
  public void fireServerStarted() {
    lifecycleEvents.fireServerStarted();
  }

  /** Fires the server stopping lifecycle phase. */
  public void fireServerStopping() {
    lifecycleEvents.fireServerStopping();
  }

  /** Returns initialized mod contexts for diagnostics and tests. */
  public List<? extends ModContext> contexts() {
    return List.copyOf(contexts);
  }
}

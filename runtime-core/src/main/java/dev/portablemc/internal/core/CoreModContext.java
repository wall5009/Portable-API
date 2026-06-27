package dev.portablemc.internal.core;

import dev.portablemc.api.attachment.AttachmentService;
import dev.portablemc.api.bootstrap.ModContext;
import dev.portablemc.api.command.CommandService;
import dev.portablemc.api.config.ConfigService;
import dev.portablemc.api.event.LifecycleEvents;
import dev.portablemc.api.logging.PortableLogger;
import dev.portablemc.api.network.NetworkService;
import dev.portablemc.api.registry.CreativeTabService;
import dev.portablemc.api.registry.RegistrationService;
import dev.portablemc.api.resource.ResourceService;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.api.runtime.PlatformInfo;
import dev.portablemc.api.runtime.SidedExecutor;
import dev.portablemc.internal.core.attachment.DefaultAttachmentService;
import dev.portablemc.internal.core.command.DefaultCommandService;
import dev.portablemc.internal.core.config.PropertiesConfigService;
import dev.portablemc.internal.core.event.DefaultLifecycleEvents;
import dev.portablemc.internal.core.logging.SystemPortableLogger;
import dev.portablemc.internal.core.network.DefaultNetworkService;
import dev.portablemc.internal.core.registry.DefaultCreativeTabService;
import dev.portablemc.internal.core.registry.DefaultRegistrationService;
import dev.portablemc.internal.core.resource.DefaultResourceService;
import java.nio.file.Path;
import java.util.Objects;

/** Default mod-scoped context. */
public final class CoreModContext implements ModContext {
  private final String modId;
  private final CorePortableRuntime runtime;
  private final DefaultLifecycleEvents lifecycleEvents;
  private final PortableLogger logger;
  private final PropertiesConfigService configService;
  private final DefaultCommandService commandService;
  private final DefaultRegistrationService registrationService;
  private final DefaultCreativeTabService creativeTabService = new DefaultCreativeTabService();
  private final DefaultNetworkService networkService = new DefaultNetworkService();
  private final DefaultResourceService resourceService = new DefaultResourceService();
  private final DefaultAttachmentService attachmentService;
  private final SidedExecutor sidedExecutor;

  CoreModContext(
      String modId,
      CorePortableRuntime runtime,
      DefaultLifecycleEvents lifecycleEvents,
      TargetBridge targetBridge) {
    this.modId = Objects.requireNonNull(modId, "modId");
    this.runtime = Objects.requireNonNull(runtime, "runtime");
    this.lifecycleEvents = Objects.requireNonNull(lifecycleEvents, "lifecycleEvents");
    this.logger = new SystemPortableLogger(modId);
    this.configService = new PropertiesConfigService(modId, Path.of("config", "portable-api"));
    this.commandService = new DefaultCommandService(runtime.capabilities());
    this.registrationService = new DefaultRegistrationService(runtime.capabilities(), targetBridge);
    this.attachmentService = new DefaultAttachmentService(runtime.capabilities());
    this.sidedExecutor = new CoreSidedExecutor(runtime.platform().physicalSide());
  }

  @Override
  public String modId() {
    return modId;
  }

  @Override
  public PlatformInfo platform() {
    return runtime.platform();
  }

  @Override
  public CapabilityReport capabilities() {
    return runtime.capabilities();
  }

  @Override
  public PortableLogger logger() {
    return logger;
  }

  @Override
  public LifecycleEvents lifecycle() {
    return lifecycleEvents;
  }

  @Override
  public ConfigService config() {
    return configService;
  }

  @Override
  public CommandService commands() {
    return commandService;
  }

  public DefaultCommandService commandService() {
    return commandService;
  }

  @Override
  public RegistrationService registrations() {
    return registrationService;
  }

  @Override
  public CreativeTabService creativeTabs() {
    return creativeTabService;
  }

  @Override
  public NetworkService networking() {
    return networkService;
  }

  @Override
  public ResourceService resources() {
    return resourceService;
  }

  @Override
  public AttachmentService attachments() {
    return attachmentService;
  }

  @Override
  public SidedExecutor sidedExecutor() {
    return sidedExecutor;
  }
}

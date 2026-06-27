package dev.portablemc.api.bootstrap;

import dev.portablemc.api.attachment.AttachmentService;
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

/** Loader-neutral access to Portable API services for one consumer mod. */
public interface ModContext {
  /** Returns the owning mod id. */
  String modId();

  /** Returns immutable platform identity and environment information. */
  PlatformInfo platform();

  /** Returns the target's capability report. */
  CapabilityReport capabilities();

  /** Returns the mod-scoped logger. */
  PortableLogger logger();

  /** Returns lifecycle event registration services. */
  LifecycleEvents lifecycle();

  /** Returns configuration registration and loading services. */
  ConfigService config();

  /** Returns command registration services. */
  CommandService commands();

  /** Returns registry and deferred supplier services. */
  RegistrationService registrations();

  /** Returns creative tab population services. */
  CreativeTabService creativeTabs();

  /** Returns packet channel services. */
  NetworkService networking();

  /** Returns reload listener and resource lookup services. */
  ResourceService resources();

  /** Returns data attachment/component services. */
  AttachmentService attachments();

  /** Returns helper methods for physically sided execution. */
  SidedExecutor sidedExecutor();
}

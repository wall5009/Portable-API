package dev.portablemc.api.resource;

import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.registry.Identifier;

/** Resource reload and lookup service. */
public interface ResourceService {
  /** Registers a server data reload listener. */
  EventHandle registerServerReloadListener(Identifier id, ResourceReloadListener listener);

  /** Registers a client resource reload listener. */
  EventHandle registerClientReloadListener(Identifier id, ResourceReloadListener listener);
}

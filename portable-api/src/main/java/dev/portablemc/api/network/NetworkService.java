package dev.portablemc.api.network;

import dev.portablemc.api.registry.Identifier;

/** Packet networking service. */
public interface NetworkService {
  /** Creates or returns a packet channel for the owning mod. */
  NetworkChannel channel(Identifier id);
}

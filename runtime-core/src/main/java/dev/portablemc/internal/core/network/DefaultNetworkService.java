package dev.portablemc.internal.core.network;

import dev.portablemc.api.network.NetworkChannel;
import dev.portablemc.api.network.NetworkService;
import dev.portablemc.api.registry.Identifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Stores packet declarations for platform adapters. */
public final class DefaultNetworkService implements NetworkService {
  private final Map<Identifier, DefaultNetworkChannel> channels = new ConcurrentHashMap<>();

  @Override
  public NetworkChannel channel(Identifier id) {
    Objects.requireNonNull(id, "id");
    return channels.computeIfAbsent(id, DefaultNetworkChannel::new);
  }
}

package dev.portablemc.internal.core.network;

import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.network.NetworkChannel;
import dev.portablemc.api.network.PacketCodec;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PacketHandler;
import dev.portablemc.api.registry.Identifier;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Default packet channel declaration store. */
public final class DefaultNetworkChannel implements NetworkChannel {
  private final Identifier id;
  private final List<PacketRegistration<?>> registrations = new CopyOnWriteArrayList<>();

  DefaultNetworkChannel(Identifier id) {
    this.id = Objects.requireNonNull(id, "id");
  }

  @Override
  public Identifier id() {
    return id;
  }

  @Override
  public <T> EventHandle registerPacket(
      Identifier packetId,
      PacketDirection direction,
      PacketCodec<T> codec,
      PacketHandler<T> handler) {
    PacketRegistration<T> registration =
        new PacketRegistration<>(packetId, direction, codec, handler);
    registrations.add(registration);
    return () -> registrations.remove(registration);
  }

  public List<PacketRegistration<?>> registrations() {
    return List.copyOf(registrations);
  }

  public record PacketRegistration<T>(
      Identifier packetId,
      PacketDirection direction,
      PacketCodec<T> codec,
      PacketHandler<T> handler) {
    public PacketRegistration {
      Objects.requireNonNull(packetId, "packetId");
      Objects.requireNonNull(direction, "direction");
      Objects.requireNonNull(codec, "codec");
      Objects.requireNonNull(handler, "handler");
    }
  }
}

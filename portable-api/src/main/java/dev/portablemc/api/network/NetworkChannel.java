package dev.portablemc.api.network;

import dev.portablemc.api.event.EventHandle;
import dev.portablemc.api.registry.Identifier;

/** Registered packet channel. */
public interface NetworkChannel {
  /** Returns the channel id. */
  Identifier id();

  /** Registers a packet payload type. */
  <T> EventHandle registerPacket(
      Identifier packetId,
      PacketDirection direction,
      PacketCodec<T> codec,
      PacketHandler<T> handler);
}

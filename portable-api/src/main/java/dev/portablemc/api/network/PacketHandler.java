package dev.portablemc.api.network;

import dev.portablemc.api.runtime.LogicalSide;

/** Handles a decoded packet. */
@FunctionalInterface
public interface PacketHandler<T> {
  /** Handles a decoded packet on the given logical side. */
  void handle(T payload, LogicalSide side);
}

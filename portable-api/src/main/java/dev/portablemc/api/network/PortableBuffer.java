package dev.portablemc.api.network;

import dev.portablemc.api.registry.Identifier;

/** Minimal byte buffer abstraction for v1 packets. */
public interface PortableBuffer {
  void writeBoolean(boolean value);

  boolean readBoolean();

  void writeInt(int value);

  int readInt();

  void writeUtf(String value);

  String readUtf();

  void writeIdentifier(Identifier value);

  Identifier readIdentifier();
}

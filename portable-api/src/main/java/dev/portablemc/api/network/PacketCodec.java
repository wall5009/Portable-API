package dev.portablemc.api.network;

/** Encodes and decodes a packet payload. */
public interface PacketCodec<T> {
  /** Encodes a payload. */
  void encode(PortableBuffer buffer, T payload);

  /** Decodes a payload. */
  T decode(PortableBuffer buffer);
}

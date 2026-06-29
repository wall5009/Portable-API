# Networking

v1.1 adds a bounded typed packet contract in `api-core`:

- `PortablePacketType<T>`
- `PortablePacketCodec<T>`
- `PortablePacketReader`
- `PortablePacketWriter`
- `PortablePacketRegistration<T>`
- `PortableEncodedPacket`
- `PortablePacketContext`

The core implementation enforces typed identifiers, protocol version checks, duplicate registration rejection, maximum payload sizes, malformed UTF-8 rejection, strict string/byte-array/collection bounds, and trailing-byte rejection.

```java
PortablePacketType<Ping> ping = context.networking().packetType(
        "ping",
        NetworkPhase.PLAY,
        PacketDirection.CLIENT_TO_SERVER,
        1,
        64,
        Ping.CODEC
);
context.networking().registerClientToServer(ping, (packet, packetContext) -> {
    packetContext.execution().executeOnMainThread(() -> {
        // safe game-thread work
    });
});
```

Boundary: loader adapters currently keep native payload send/receive APIs explicit. Code that depends on connection tracking, arbitrary broadcast, or loader-specific payload phases belongs in a platform module until a safe common contract exists for all four targets.

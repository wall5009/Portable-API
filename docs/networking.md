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

Loader bridges bind this contract to Fabric 1.20.1, Fabric 1.21.1, Forge 1.20.1, and NeoForge 1.21.1 native payload APIs. The wire envelope is validated before dispatch, so malformed packets, wrong directions, wrong phases, protocol mismatches, and oversized payloads fail before user handlers run.

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

Client code sends portable C2S packets with `context.networking().sendToServer(type, value)`. Server handlers can reply to the sender with `context.networking().sendToPlayer(sender, type, value)` or `sender.send(type, value)` when `packetContext.sender()` is present.

Boundary: code that depends on connection tracking, arbitrary broadcast, login/configuration payload phases, or loader-specific channel negotiation belongs in a platform module until a safe common contract exists for all four targets.

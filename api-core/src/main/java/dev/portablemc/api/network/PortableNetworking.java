/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Networking foundation for protocol declaration and bounded typed packets.
 *
 * <p>Portable API validates packet ids, protocol versions, payload sizes, and
 * duplicate registrations in core. Loader adapters remain responsible for
 * binding those registrations to the platform's actual payload registration
 * mechanism.</p>
 */
public final class PortableNetworking {
    private final String modId;
    private final PortableNetworkingAdapter adapter;
    private final Set<PortableIdentifier> channels = new HashSet<>();
    private final Map<RegistrationKey, PortablePacketRegistration<?>> registrations = new ConcurrentHashMap<>();

    /**
     * Creates a networking service.
     *
     * @param modId owning mod id
     * @param adapter loader adapter
     */
    public PortableNetworking(final String modId, final PortableNetworkingAdapter adapter) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    /**
     * Declares a channel so loader adapters can register or validate protocol
     * metadata at the correct lifecycle point.
     *
     * @param path channel path under the owning mod id
     * @param phase protocol phase
     * @param protocolVersion caller-defined protocol version
     * @param maxPayloadBytes maximum accepted payload size
     * @return declared channel metadata
     */
    public PortableNetworkChannel declareChannel(
            final String path,
            final NetworkPhase phase,
            final int protocolVersion,
            final int maxPayloadBytes
    ) {
        PortableIdentifier id = PortableIdentifier.of(modId, path);
        if (!channels.add(id)) {
            throw new IllegalStateException("Duplicate network channel declaration for " + id);
        }
        PortableNetworkChannel channel = new PortableNetworkChannel(id, phase, protocolVersion, maxPayloadBytes);
        adapter.declare(channel);
        return channel;
    }

    /**
     * Creates a typed packet declaration under the owning mod id.
     *
     * @param path packet path
     * @param phase network phase
     * @param direction packet direction
     * @param protocolVersion expected protocol version
     * @param maxPayloadBytes maximum accepted payload size
     * @param codec packet codec
     * @param <T> packet value type
     * @return packet type
     */
    public <T> PortablePacketType<T> packetType(
            final String path,
            final NetworkPhase phase,
            final PacketDirection direction,
            final int protocolVersion,
            final int maxPayloadBytes,
            final PortablePacketCodec<T> codec
    ) {
        return new PortablePacketType<>(
                PortableIdentifier.of(modId, path),
                phase,
                direction,
                protocolVersion,
                maxPayloadBytes,
                codec
        );
    }

    /**
     * Registers a client-to-server packet handler.
     *
     * @param type packet type
     * @param handler packet handler
     * @param <T> packet value type
     */
    public <T> void registerClientToServer(
            final PortablePacketType<T> type,
            final PortablePacketHandler<T> handler
    ) {
        register(PacketDirection.CLIENT_TO_SERVER, type, handler);
    }

    /**
     * Registers a server-to-client packet handler.
     *
     * @param type packet type
     * @param handler packet handler
     * @param <T> packet value type
     */
    public <T> void registerServerToClient(
            final PortablePacketType<T> type,
            final PortablePacketHandler<T> handler
    ) {
        register(PacketDirection.SERVER_TO_CLIENT, type, handler);
    }

    /**
     * Sends a client-to-server packet from a physical client.
     *
     * @param type client-to-server packet type
     * @param packet packet value
     * @param <T> packet value type
     */
    public <T> void sendToServer(final PortablePacketType<T> type, final T packet) {
        requireDirection(type, PacketDirection.CLIENT_TO_SERVER);
        adapter.sendToServer(encode(type, packet));
    }

    /**
     * Sends a server-to-client packet to one portable packet sender.
     *
     * <p>This is equivalent to calling {@link PortablePacketSender#send(PortablePacketType, Object)}
     * and is provided for call sites that keep all networking operations on the
     * networking service.</p>
     *
     * @param sender target player sender
     * @param type server-to-client packet type
     * @param packet packet value
     * @param <T> packet value type
     */
    public <T> void sendToPlayer(
            final PortablePacketSender sender,
            final PortablePacketType<T> type,
            final T packet
    ) {
        Objects.requireNonNull(sender, "sender");
        requireDirection(type, PacketDirection.SERVER_TO_CLIENT);
        sender.send(type, packet);
    }

    /**
     * Encodes a packet using core bounds and protocol metadata.
     *
     * @param type packet type
     * @param packet packet value
     * @param <T> packet value type
     * @return encoded packet
     */
    public <T> PortableEncodedPacket encode(final PortablePacketType<T> type, final T packet) {
        Objects.requireNonNull(type, "type");
        return new PortableEncodedPacket(
                type.id(),
                type.phase(),
                type.direction(),
                type.protocolVersion(),
                PortablePacketBuffer.encode(type, packet)
        );
    }

    /**
     * Dispatches one encoded packet through a registered handler. Loader
     * adapters and tests use this after receiving raw payload bytes.
     *
     * @param packet encoded packet
     * @param context packet context
     */
    public void dispatch(final PortableEncodedPacket packet, final PortablePacketContext context) {
        Objects.requireNonNull(packet, "packet");
        PortablePacketRegistration<?> registration = registrations.get(new RegistrationKey(packet.id(), packet.direction()));
        if (registration == null) {
            throw new PortablePacketException("No portable packet handler registered for " + packet.direction() + " " + packet.id());
        }
        registration.dispatch(packet, context);
    }

    private <T> void register(
            final PacketDirection expectedDirection,
            final PortablePacketType<T> type,
            final PortablePacketHandler<T> handler
    ) {
        requireDirection(type, expectedDirection);
        PortablePacketRegistration<T> registration = new PortablePacketRegistration<>(type, handler);
        RegistrationKey key = new RegistrationKey(type.id(), type.direction());
        if (registrations.putIfAbsent(key, registration) != null) {
            throw new IllegalStateException("Duplicate packet registration for " + type.direction() + " " + type.id());
        }
        adapter.registerPacket(registration);
    }

    private static void requireDirection(final PortablePacketType<?> type, final PacketDirection expectedDirection) {
        Objects.requireNonNull(type, "type");
        if (type.direction() != expectedDirection) {
            throw new IllegalArgumentException("Packet " + type.id() + " must be " + expectedDirection + " but is " + type.direction());
        }
    }

    private record RegistrationKey(PortableIdentifier id, PacketDirection direction) {
        private RegistrationKey {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(direction, "direction");
        }
    }
}

/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.neoforge1211;

import dev.portablemc.api.mc1211.Minecraft1211Adapters;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortablePacketBuffer;
import dev.portablemc.api.network.PortablePacketException;
import dev.portablemc.api.network.PortablePacketRegistration;
import dev.portablemc.api.network.PortablePacketType;
import dev.portablemc.api.network.PortablePacketWireFormat;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * NeoForge 1.21.1 custom payload wrapper for one portable packet type.
 */
final class NeoForge1211PortablePayload implements CustomPacketPayload {
    private final CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType;
    private final PortableEncodedPacket packet;

    NeoForge1211PortablePayload(
            final CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType,
            final PortableEncodedPacket packet
    ) {
        this.payloadType = Objects.requireNonNull(payloadType, "payloadType");
        this.packet = Objects.requireNonNull(packet, "packet");
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return payloadType;
    }

    PortableEncodedPacket packet() {
        return packet;
    }

    static CustomPacketPayload.Type<NeoForge1211PortablePayload> type(final PortablePacketRegistration<?> registration) {
        return new CustomPacketPayload.Type<>(Minecraft1211Adapters.resourceLocation(registration.type().id()));
    }

    static StreamCodec<RegistryFriendlyByteBuf, NeoForge1211PortablePayload> codec(
            final CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType,
            final PortablePacketRegistration<?> registration
    ) {
        return StreamCodec.of(
                (buffer, payload) -> buffer.writeByteArray(PortablePacketWireFormat.encode(payload.packet())),
                buffer -> {
                    byte[] wireBytes = buffer.readByteArray(registration.type().maxPayloadBytes() + 2048);
                    return new NeoForge1211PortablePayload(payloadType, PortablePacketWireFormat.decode(registration, wireBytes));
                }
        );
    }

    static <T> NeoForge1211PortablePayload encode(
            final CustomPacketPayload.Type<NeoForge1211PortablePayload> payloadType,
            final PortablePacketType<T> packetType,
            final T packet
    ) {
        if (packetType.direction() != PacketDirection.SERVER_TO_CLIENT
                && packetType.direction() != PacketDirection.CLIENT_TO_SERVER) {
            throw new PortablePacketException("Unsupported portable packet direction " + packetType.direction());
        }
        return new NeoForge1211PortablePayload(payloadType, new PortableEncodedPacket(
                packetType.id(),
                packetType.phase(),
                packetType.direction(),
                packetType.protocolVersion(),
                PortablePacketBuffer.encode(packetType, packet)
        ));
    }
}

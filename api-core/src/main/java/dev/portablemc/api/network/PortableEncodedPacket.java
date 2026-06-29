/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.InternalApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Encoded packet passed from core Portable API to loader adapters.
 *
 * @param id packet id
 * @param phase network phase
 * @param direction packet direction
 * @param protocolVersion protocol version
 * @param payload encoded payload bytes
 */
@InternalApi
@Since("1.1.0")
public record PortableEncodedPacket(
        PortableIdentifier id,
        NetworkPhase phase,
        PacketDirection direction,
        int protocolVersion,
        byte[] payload
) {
    /**
     * Validates and defensively copies an encoded packet.
     */
    public PortableEncodedPacket {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(phase, "phase");
        Objects.requireNonNull(direction, "direction");
        payload = Objects.requireNonNull(payload, "payload").clone();
        if (protocolVersion < 1) {
            throw new IllegalArgumentException("protocolVersion must be positive: " + protocolVersion);
        }
    }

    /**
     * Returns a defensive copy of the payload.
     *
     * @return payload copy
     */
    @Override
    public byte[] payload() {
        return payload.clone();
    }
}

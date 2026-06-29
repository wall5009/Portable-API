/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Typed portable packet declaration.
 *
 * @param id packet id
 * @param phase network phase
 * @param direction packet direction
 * @param protocolVersion expected protocol version
 * @param maxPayloadBytes maximum accepted encoded payload
 * @param codec packet codec
 * @param <T> packet value type
 */
@PublicApi
@Since("1.1.0")
public record PortablePacketType<T>(
        PortableIdentifier id,
        NetworkPhase phase,
        PacketDirection direction,
        int protocolVersion,
        int maxPayloadBytes,
        PortablePacketCodec<T> codec
) {
    /**
     * Validates a packet declaration.
     */
    public PortablePacketType {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(phase, "phase");
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(codec, "codec");
        if (protocolVersion < 1) {
            throw new IllegalArgumentException("protocolVersion must be positive: " + protocolVersion);
        }
        if (maxPayloadBytes < 1) {
            throw new IllegalArgumentException("maxPayloadBytes must be positive: " + maxPayloadBytes);
        }
    }
}

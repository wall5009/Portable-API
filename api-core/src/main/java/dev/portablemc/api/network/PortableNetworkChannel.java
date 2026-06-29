/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import java.util.Objects;

/**
 * Declared network channel metadata.
 *
 * @param id channel id
 * @param phase protocol phase
 * @param protocolVersion caller-defined protocol version
 * @param maxPayloadBytes maximum supported payload size
 */
public record PortableNetworkChannel(
        PortableIdentifier id,
        NetworkPhase phase,
        int protocolVersion,
        int maxPayloadBytes
) {
    /**
     * Validates a channel declaration.
     */
    public PortableNetworkChannel {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(phase, "phase");
        if (protocolVersion < 1) {
            throw new IllegalArgumentException("protocolVersion must be positive: " + protocolVersion);
        }
        if (maxPayloadBytes < 1) {
            throw new IllegalArgumentException("maxPayloadBytes must be positive: " + maxPayloadBytes);
        }
    }
}

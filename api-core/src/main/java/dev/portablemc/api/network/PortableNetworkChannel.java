/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

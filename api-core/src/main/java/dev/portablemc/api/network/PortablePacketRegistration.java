/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.InternalApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;

/**
 * Fully validated packet registration handed to loader adapters.
 *
 * @param type packet type
 * @param handler decoded packet handler
 * @param <T> packet value type
 */
@InternalApi
@Since("1.1.0")
public record PortablePacketRegistration<T>(
        PortablePacketType<T> type,
        PortablePacketHandler<T> handler
) {
    /**
     * Creates a packet registration.
     */
    public PortablePacketRegistration {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
    }

    /**
     * Decodes, validates, and dispatches one encoded packet.
     *
     * @param encoded encoded packet
     * @param context packet context
     */
    public void dispatch(final PortableEncodedPacket encoded, final PortablePacketContext context) {
        Objects.requireNonNull(encoded, "encoded");
        Objects.requireNonNull(context, "context");
        if (!type.id().equals(encoded.id())) {
            throw new PortablePacketException("Packet id mismatch. Expected " + type.id() + " but received " + encoded.id());
        }
        if (type.direction() != encoded.direction()) {
            throw new PortablePacketException("Packet direction mismatch for " + type.id());
        }
        if (type.protocolVersion() != encoded.protocolVersion()) {
            throw new PortablePacketException(
                    "Protocol version mismatch for " + type.id() + ": expected "
                            + type.protocolVersion() + " but received " + encoded.protocolVersion()
            );
        }
        T decoded = PortablePacketBuffer.decode(type, encoded.payload());
        handler.handle(decoded, context);
    }
}

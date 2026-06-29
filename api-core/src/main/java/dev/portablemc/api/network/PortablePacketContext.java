/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Objects;
import java.util.Optional;

/**
 * Context passed to portable packet handlers.
 *
 * @param direction direction of the handled packet
 * @param sender peer that sent the packet, when available
 * @param execution safe execution bridge
 */
@PublicApi
@Since("1.1.0")
public record PortablePacketContext(
        PacketDirection direction,
        Optional<PortablePacketSender> sender,
        PortableExecutionContext execution
) {
    /**
     * Validates a packet context.
     */
    public PortablePacketContext {
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(sender, "sender");
        Objects.requireNonNull(execution, "execution");
    }
}

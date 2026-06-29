/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Optional;
import java.util.UUID;

/**
 * Portable representation of the peer that sent a packet. For client-to-server
 * packets this is the server-side player connection, and handlers may use it to
 * send an immediate server-to-client response.
 */
@PublicApi
@Since("1.1.0")
public interface PortablePacketSender {
    /**
     * Returns the player UUID when the loader exposes one.
     *
     * @return optional UUID
     */
    Optional<UUID> playerId();

    /**
     * Returns the display name when the loader exposes one.
     *
     * @return optional display name
     */
    Optional<String> displayName();

    /**
     * Sends a server-to-client packet to this peer.
     *
     * @param type server-to-client packet type
     * @param packet packet value
     * @param <T> packet value type
     */
    <T> void send(PortablePacketType<T> type, T packet);
}

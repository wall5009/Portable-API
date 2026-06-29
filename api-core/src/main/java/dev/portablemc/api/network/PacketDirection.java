/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Direction of a play-phase portable packet.
 */
@PublicApi
@Since("1.1.0")
public enum PacketDirection {
    /** Packet sent from a logical client to the logical server. */
    CLIENT_TO_SERVER,

    /** Packet sent from the logical server to one logical client. */
    SERVER_TO_CLIENT
}

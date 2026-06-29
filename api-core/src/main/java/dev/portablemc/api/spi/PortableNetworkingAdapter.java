/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.spi;

import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortableNetworkChannel;
import dev.portablemc.api.network.PortablePacketRegistration;

/**
 * Service-provider interface for network channel declarations.
 */
public interface PortableNetworkingAdapter {
    /**
     * Declares a channel with the active platform.
     *
     * @param channel channel metadata
     */
    void declare(PortableNetworkChannel channel);

    /**
     * Registers a decoded portable packet handler. Loader adapters should bind
     * this registration to the correct platform payload-registration API for
     * the packet direction and phase, then call
     * {@link PortablePacketRegistration#dispatch(PortableEncodedPacket, dev.portablemc.api.network.PortablePacketContext)}
     * after receiving raw bytes from the platform.
     *
     * @param registration packet registration
     * @param <T> packet value type
     */
    default <T> void registerPacket(final PortablePacketRegistration<T> registration) {
        throw new UnsupportedOperationException("Portable packet registration is not implemented by this adapter");
    }

    /**
     * Sends one client-to-server packet from the physical client.
     *
     * @param packet encoded packet
     */
    default void sendToServer(final PortableEncodedPacket packet) {
        throw new UnsupportedOperationException("Client-to-server portable packet sending is not implemented by this adapter");
    }
}

/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Handles a decoded portable packet.
 *
 * @param <T> packet value type
 */
@FunctionalInterface
@PublicApi
@Since("1.1.0")
public interface PortablePacketHandler<T> {
    /**
     * Handles the decoded packet. Handlers may call
     * {@link PortableExecutionContext#executeOnMainThread(Runnable)} when work
     * must run on the game thread.
     *
     * @param packet decoded packet
     * @param context portable packet context
     */
    void handle(T packet, PortablePacketContext context);
}

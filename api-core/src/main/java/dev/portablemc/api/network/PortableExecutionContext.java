/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Portable execution bridge supplied by loader adapters during packet handling.
 */
@PublicApi
@Since("1.1.0")
public interface PortableExecutionContext {
    /**
     * Schedules work on the loader's safe main game thread.
     *
     * @param work work to schedule
     */
    void executeOnMainThread(Runnable work);
}

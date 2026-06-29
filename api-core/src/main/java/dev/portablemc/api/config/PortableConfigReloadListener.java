/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Listener called after a typed config snapshot is reloaded.
 */
@FunctionalInterface
@PublicApi
@Since("1.1.0")
public interface PortableConfigReloadListener {
    /**
     * Runs after a successful reload. Recovery to defaults is considered
     * successful and can be inspected through {@link PortableConfigHandle#warnings()}.
     *
     * @param snapshot new immutable snapshot
     */
    void onReload(PortableConfigSnapshot snapshot);
}

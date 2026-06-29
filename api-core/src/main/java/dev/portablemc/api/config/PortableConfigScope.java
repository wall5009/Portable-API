/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.config;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Portable configuration scopes with equivalent mappings across all supported
 * loaders.
 */
@PublicApi
@Since("1.1.0")
public enum PortableConfigScope {
    /**
     * File stored directly in the loader's normal config directory. This is the
     * only v1.1 portable scope because every supported target exposes it with
     * equivalent lifetime and visibility.
     */
    GLOBAL
}

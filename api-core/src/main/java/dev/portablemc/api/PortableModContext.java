/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import dev.portablemc.api.command.PortableCommandManager;
import dev.portablemc.api.config.PortableConfigManager;
import dev.portablemc.api.content.PortableContentRegistry;
import dev.portablemc.api.network.PortableNetworking;

/**
 * Shared-code view of the active Portable API runtime.
 */
public interface PortableModContext {
    /**
     * Returns the owning mod id.
     *
     * @return mod id used as the default namespace
     */
    String modId();

    /**
     * Returns information about the loader, Minecraft version, and side.
     *
     * @return platform information
     */
    PlatformInfo platform();

    /**
     * Returns a logger scoped to the owning mod.
     *
     * @return logger facade
     */
    PortableLogger logger();

    /**
     * Returns lifecycle callback registration hooks.
     *
     * @return lifecycle event registry
     */
    PortableLifecycleEvents lifecycle();

    /**
     * Returns basic block, item, and creative-tab registration services.
     *
     * @return content registry
     */
    PortableContentRegistry content();

    /**
     * Returns command registration services.
     *
     * @return command manager
     */
    PortableCommandManager commands();

    /**
     * Returns configuration file hooks.
     *
     * @return config manager
     */
    PortableConfigManager config();

    /**
     * Returns networking declarations for protocol setup.
     *
     * @return networking service
     */
    PortableNetworking networking();

    /**
     * Returns optional loader/platform services beyond the stable
     * {@link PlatformInfo} record. The default preserves v1.0 implementations
     * by exposing only information already present in {@link #platform()}.
     *
     * @return platform services
     */
    default PortablePlatformServices platformServices() {
        return PortablePlatformServices.basic(platform());
    }

    /**
     * Returns a low-noise diagnostic helper for the active Portable API
     * context.
     *
     * @return diagnostics
     */
    default PortableDiagnostics diagnostics() {
        return new PortableDiagnostics(this);
    }
}

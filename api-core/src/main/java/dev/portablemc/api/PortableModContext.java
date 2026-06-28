/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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
}

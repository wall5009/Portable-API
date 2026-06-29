/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.internal;

import dev.portablemc.api.PlatformInfo;
import dev.portablemc.api.PortableLifecycleEvents;
import dev.portablemc.api.PortableLogger;
import dev.portablemc.api.PortableModContext;
import dev.portablemc.api.PortablePlatformServices;
import dev.portablemc.api.command.PortableCommandManager;
import dev.portablemc.api.config.PortableConfigManager;
import dev.portablemc.api.content.PortableContentRegistry;
import dev.portablemc.api.network.PortableNetworking;
import java.util.Objects;

/**
 * Default immutable context assembled by loader bootstraps.
 */
public final class DefaultPortableModContext implements PortableModContext {
    private final String modId;
    private final PlatformInfo platform;
    private final PortableLogger logger;
    private final PortableLifecycleEvents lifecycle;
    private final PortableContentRegistry content;
    private final PortableCommandManager commands;
    private final PortableConfigManager config;
    private final PortableNetworking networking;
    private final PortablePlatformServices platformServices;

    /**
     * Creates a context from already-adapted services.
     */
    public DefaultPortableModContext(
            final String modId,
            final PlatformInfo platform,
            final PortableLogger logger,
            final PortableLifecycleEvents lifecycle,
            final PortableContentRegistry content,
            final PortableCommandManager commands,
            final PortableConfigManager config,
            final PortableNetworking networking
    ) {
        this(modId, platform, logger, lifecycle, content, commands, config, networking, PortablePlatformServices.basic(platform));
    }

    /**
     * Creates a context from already-adapted services and optional platform
     * service queries.
     */
    public DefaultPortableModContext(
            final String modId,
            final PlatformInfo platform,
            final PortableLogger logger,
            final PortableLifecycleEvents lifecycle,
            final PortableContentRegistry content,
            final PortableCommandManager commands,
            final PortableConfigManager config,
            final PortableNetworking networking,
            final PortablePlatformServices platformServices
    ) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.platform = Objects.requireNonNull(platform, "platform");
        this.logger = Objects.requireNonNull(logger, "logger");
        this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
        this.content = Objects.requireNonNull(content, "content");
        this.commands = Objects.requireNonNull(commands, "commands");
        this.config = Objects.requireNonNull(config, "config");
        this.networking = Objects.requireNonNull(networking, "networking");
        this.platformServices = Objects.requireNonNull(platformServices, "platformServices");
    }

    @Override
    public String modId() {
        return modId;
    }

    @Override
    public PlatformInfo platform() {
        return platform;
    }

    @Override
    public PortableLogger logger() {
        return logger;
    }

    @Override
    public PortableLifecycleEvents lifecycle() {
        return lifecycle;
    }

    @Override
    public PortableContentRegistry content() {
        return content;
    }

    @Override
    public PortableCommandManager commands() {
        return commands;
    }

    @Override
    public PortableConfigManager config() {
        return config;
    }

    @Override
    public PortableNetworking networking() {
        return networking;
    }

    @Override
    public PortablePlatformServices platformServices() {
        return platformServices;
    }
}

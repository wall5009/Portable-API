/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable description of the active loader, Minecraft version, and physical side.
 *
 * @param loader loader family
 * @param minecraftVersion exact Minecraft target
 * @param side physical runtime side
 * @param gameDirectory game directory exposed by the loader, when available
 * @param configDirectory configuration directory exposed by the loader
 */
public record PlatformInfo(
        LoaderKind loader,
        MinecraftVersion minecraftVersion,
        RuntimeSide side,
        Optional<Path> gameDirectory,
        Path configDirectory
) {
    /**
     * Creates platform information and validates that no optional field is
     * represented by {@code null}. Loader modules build this once during their
     * bootstrap path and then pass it through to shared code.
     */
    public PlatformInfo {
        Objects.requireNonNull(loader, "loader");
        Objects.requireNonNull(minecraftVersion, "minecraftVersion");
        Objects.requireNonNull(side, "side");
        Objects.requireNonNull(gameDirectory, "gameDirectory");
        Objects.requireNonNull(configDirectory, "configDirectory");
    }

    /**
     * Returns {@code true} when this process can safely load client-only classes.
     *
     * @return whether the physical runtime is a client
     */
    public boolean isClient() {
        return side == RuntimeSide.CLIENT;
    }

    /**
     * Returns {@code true} when this process is a dedicated server.
     *
     * @return whether the physical runtime is server-only
     */
    public boolean isDedicatedServer() {
        return side == RuntimeSide.DEDICATED_SERVER;
    }
}

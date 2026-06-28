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

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

/**
 * Entry point implemented by shared mod code.
 *
 * <p>Loader-specific entrypoints should be intentionally thin: construct the
 * shared mod object and pass it to the matching Portable API bootstrap class.
 * The bootstrap creates a {@link PortableModContext} whose services are backed
 * by the active loader and Minecraft version.</p>
 */
@FunctionalInterface
public interface PortableMod {
    /**
     * Called exactly once by a loader bootstrap during mod construction or
     * initialization. Register content, commands, config hooks, lifecycle
     * callbacks, and protocol declarations here.
     *
     * @param context loader-backed context for this mod
     */
    void initialize(PortableModContext context);
}

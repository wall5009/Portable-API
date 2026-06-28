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
 * Physical side of the currently running process.
 *
 * <p>This is not a logical-side abstraction. A dedicated server reports
 * {@link #DEDICATED_SERVER}; an integrated server inside a client reports
 * {@link #CLIENT} because client-only classes are present in that process.</p>
 */
public enum RuntimeSide {
    /** A Minecraft client process, including single-player integrated server runs. */
    CLIENT,

    /** A headless dedicated server process. */
    DEDICATED_SERVER
}

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
package dev.portablemc.api.spi;

import dev.portablemc.api.command.PortableCommand;

/**
 * Service-provider interface used by loader modules to receive command declarations.
 */
public interface PortableCommandAdapter {
    /**
     * Registers a command declaration with the active loader bridge.
     *
     * @param command command declaration
     */
    void register(PortableCommand command);
}

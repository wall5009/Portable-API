/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Thrown when portable packet encoding, decoding, validation, or dispatch
 * cannot be completed safely.
 */
@PublicApi
@Since("1.1.0")
public class PortablePacketException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a packet exception.
     *
     * @param message diagnostic message
     */
    public PortablePacketException(final String message) {
        super(message);
    }

    /**
     * Creates a packet exception with a cause.
     *
     * @param message diagnostic message
     * @param cause underlying cause
     */
    public PortablePacketException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

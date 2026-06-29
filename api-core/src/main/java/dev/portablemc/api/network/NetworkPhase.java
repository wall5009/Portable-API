/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

/**
 * Protocol phase for a declared networking channel.
 */
public enum NetworkPhase {
    /** Login/configuration-time negotiation. */
    CONFIGURATION,

    /** Normal in-world play traffic. */
    PLAY
}

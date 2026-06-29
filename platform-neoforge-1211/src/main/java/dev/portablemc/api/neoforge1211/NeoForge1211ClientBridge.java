/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.neoforge1211;

import dev.portablemc.api.internal.DefaultPortableModContext;
import java.util.Objects;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Client-only NeoForge 1.21.1 lifecycle bridge.
 */
final class NeoForge1211ClientBridge {
    private NeoForge1211ClientBridge() {
    }

    static void install(final DefaultPortableModContext context) {
        Objects.requireNonNull(context, "context");
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> context.lifecycle().fireClientTick());
    }
}

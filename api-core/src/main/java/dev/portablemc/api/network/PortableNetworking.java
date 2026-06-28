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
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Networking foundation for protocol declaration.
 *
 * <p>V1 deliberately stops at declaring channels and protocol metadata. Fabric
 * 1.20.1, Fabric 1.21.1, Forge 1.20.1, and NeoForge 1.21.1 expose meaningfully
 * different payload registration APIs. Portable API will not hide those
 * differences behind a fake universal send/receive API until a safe adapter
 * contract exists.</p>
 */
public final class PortableNetworking {
    private final String modId;
    private final PortableNetworkingAdapter adapter;
    private final Set<PortableIdentifier> channels = new HashSet<>();

    /**
     * Creates a networking service.
     *
     * @param modId owning mod id
     * @param adapter loader adapter
     */
    public PortableNetworking(final String modId, final PortableNetworkingAdapter adapter) {
        this.modId = Objects.requireNonNull(modId, "modId");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
    }

    /**
     * Declares a channel so loader adapters can register or validate protocol
     * metadata at the correct lifecycle point.
     *
     * @param path channel path under the owning mod id
     * @param phase protocol phase
     * @param protocolVersion caller-defined protocol version
     * @param maxPayloadBytes maximum accepted payload size
     * @return declared channel metadata
     */
    public PortableNetworkChannel declareChannel(
            final String path,
            final NetworkPhase phase,
            final int protocolVersion,
            final int maxPayloadBytes
    ) {
        PortableIdentifier id = PortableIdentifier.of(modId, path);
        if (!channels.add(id)) {
            throw new IllegalStateException("Duplicate network channel declaration for " + id);
        }
        PortableNetworkChannel channel = new PortableNetworkChannel(id, phase, protocolVersion, maxPayloadBytes);
        adapter.declare(channel);
        return channel;
    }
}

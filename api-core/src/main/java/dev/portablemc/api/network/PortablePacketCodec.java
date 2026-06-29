/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;

/**
 * Codec for one portable packet type. Codecs must be deterministic, bounded,
 * and must not use Java object serialization.
 *
 * @param <T> packet value type
 */
@PublicApi
@Since("1.1.0")
public interface PortablePacketCodec<T> {
    /**
     * Encodes one packet value.
     *
     * @param writer bounded packet writer
     * @param packet packet value
     */
    void encode(PortablePacketWriter writer, T packet);

    /**
     * Decodes one packet value.
     *
     * @param reader bounded packet reader
     * @return decoded packet
     */
    T decode(PortablePacketReader reader);
}

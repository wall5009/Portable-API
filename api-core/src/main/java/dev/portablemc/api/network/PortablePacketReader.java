/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.List;
import java.util.function.Function;

/**
 * Bounded primitive reader used by portable packet codecs.
 */
@PublicApi
@Since("1.1.0")
public interface PortablePacketReader {
    /**
     * Reads a boolean.
     *
     * @return decoded value
     */
    boolean readBoolean();

    /**
     * Reads a 32-bit integer.
     *
     * @return decoded value
     */
    int readInt();

    /**
     * Reads a 64-bit integer.
     *
     * @return decoded value
     */
    long readLong();

    /**
     * Reads a 64-bit floating-point value.
     *
     * @return decoded value
     */
    double readDouble();

    /**
     * Reads a UTF-8 string with an explicit encoded-byte limit.
     *
     * @param maxUtf8Bytes maximum allowed encoded bytes
     * @return decoded string
     */
    String readString(int maxUtf8Bytes);

    /**
     * Reads a byte array with an explicit length limit.
     *
     * @param maxBytes maximum allowed byte count
     * @return decoded bytes
     */
    byte[] readByteArray(int maxBytes);

    /**
     * Reads a portable identifier.
     *
     * @return decoded identifier
     */
    PortableIdentifier readIdentifier();

    /**
     * Reads a bounded list.
     *
     * @param maxElements maximum allowed elements
     * @param elementReader element reader
     * @param <T> element type
     * @return decoded immutable list
     */
    <T> List<T> readList(int maxElements, Function<PortablePacketReader, T> elementReader);

    /**
     * Fails when unread bytes remain.
     */
    void requireFullyRead();
}

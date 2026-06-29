/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Bounded primitive writer used by portable packet codecs.
 */
@PublicApi
@Since("1.1.0")
public interface PortablePacketWriter {
    /**
     * Writes a boolean.
     *
     * @param value value to write
     */
    void writeBoolean(boolean value);

    /**
     * Writes a 32-bit integer.
     *
     * @param value value to write
     */
    void writeInt(int value);

    /**
     * Writes a 64-bit integer.
     *
     * @param value value to write
     */
    void writeLong(long value);

    /**
     * Writes a 64-bit floating-point value.
     *
     * @param value value to write
     */
    void writeDouble(double value);

    /**
     * Writes a UTF-8 string with an explicit encoded-byte limit.
     *
     * @param value string value
     * @param maxUtf8Bytes maximum allowed encoded bytes
     */
    void writeString(String value, int maxUtf8Bytes);

    /**
     * Writes a byte array with an explicit length limit.
     *
     * @param value bytes to write
     * @param maxBytes maximum allowed byte count
     */
    void writeByteArray(byte[] value, int maxBytes);

    /**
     * Writes a portable identifier as its canonical string form.
     *
     * @param value identifier
     */
    void writeIdentifier(PortableIdentifier value);

    /**
     * Writes a collection with an explicit element-count limit.
     *
     * @param values collection to write
     * @param maxElements maximum allowed elements
     * @param elementWriter element writer
     * @param <T> element type
     */
    <T> void writeCollection(Collection<T> values, int maxElements, BiConsumer<PortablePacketWriter, T> elementWriter);
}

/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.PublicApi;
import dev.portablemc.api.stability.Since;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Utility entry point for encoding and decoding portable packet payload bytes.
 */
@PublicApi
@Since("1.1.0")
public final class PortablePacketBuffer {
    private static final int IDENTIFIER_MAX_BYTES = 512;

    private PortablePacketBuffer() {
    }

    /**
     * Encodes a packet and validates the final payload size.
     *
     * @param type packet type
     * @param packet packet value
     * @param <T> packet value type
     * @return encoded payload
     */
    public static <T> byte[] encode(final PortablePacketType<T> type, final T packet) {
        Objects.requireNonNull(type, "type");
        BoundedWriter writer = new BoundedWriter(type.maxPayloadBytes());
        type.codec().encode(writer, packet);
        return writer.toByteArray();
    }

    /**
     * Decodes a packet and rejects unread trailing bytes.
     *
     * @param type packet type
     * @param payload encoded payload
     * @param <T> packet value type
     * @return decoded packet
     */
    public static <T> T decode(final PortablePacketType<T> type, final byte[] payload) {
        Objects.requireNonNull(type, "type");
        byte[] copy = Objects.requireNonNull(payload, "payload").clone();
        if (copy.length > type.maxPayloadBytes()) {
            throw new PortablePacketException(
                    "Packet " + type.id() + " exceeds maximum payload of " + type.maxPayloadBytes() + " bytes"
            );
        }
        BoundedReader reader = new BoundedReader(copy);
        T decoded = type.codec().decode(reader);
        reader.requireFullyRead();
        return decoded;
    }

    private static final class BoundedWriter implements PortablePacketWriter {
        private final int maxPayloadBytes;
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private final DataOutputStream output = new DataOutputStream(bytes);

        private BoundedWriter(final int maxPayloadBytes) {
            this.maxPayloadBytes = maxPayloadBytes;
        }

        @Override
        public void writeBoolean(final boolean value) {
            writeIo(() -> output.writeBoolean(value));
        }

        @Override
        public void writeInt(final int value) {
            writeIo(() -> output.writeInt(value));
        }

        @Override
        public void writeLong(final long value) {
            writeIo(() -> output.writeLong(value));
        }

        @Override
        public void writeDouble(final double value) {
            writeIo(() -> output.writeDouble(value));
        }

        @Override
        public void writeString(final String value, final int maxUtf8Bytes) {
            byte[] encoded = Objects.requireNonNull(value, "value").getBytes(StandardCharsets.UTF_8);
            validateLimit("string", encoded.length, maxUtf8Bytes);
            writeInt(encoded.length);
            writeIo(() -> output.write(encoded));
        }

        @Override
        public void writeByteArray(final byte[] value, final int maxBytes) {
            byte[] copy = Objects.requireNonNull(value, "value").clone();
            validateLimit("byte array", copy.length, maxBytes);
            writeInt(copy.length);
            writeIo(() -> output.write(copy));
        }

        @Override
        public void writeIdentifier(final PortableIdentifier value) {
            writeString(Objects.requireNonNull(value, "value").asString(), IDENTIFIER_MAX_BYTES);
        }

        @Override
        public <T> void writeCollection(
                final Collection<T> values,
                final int maxElements,
                final BiConsumer<PortablePacketWriter, T> elementWriter
        ) {
            Objects.requireNonNull(values, "values");
            Objects.requireNonNull(elementWriter, "elementWriter");
            validateLimit("collection", values.size(), maxElements);
            writeInt(values.size());
            values.forEach(value -> elementWriter.accept(this, value));
        }

        private byte[] toByteArray() {
            byte[] encoded = bytes.toByteArray();
            validateLimit("packet payload", encoded.length, maxPayloadBytes);
            return encoded;
        }

        private void writeIo(final IoRunnable runnable) {
            try {
                runnable.run();
                validateLimit("packet payload", bytes.size(), maxPayloadBytes);
            } catch (IOException exception) {
                throw new PortablePacketException("Failed to encode packet payload", exception);
            }
        }
    }

    private static final class BoundedReader implements PortablePacketReader {
        private final byte[] payload;
        private final DataInputStream input;

        private BoundedReader(final byte[] payload) {
            this.payload = payload;
            this.input = new DataInputStream(new ByteArrayInputStream(payload));
        }

        @Override
        public boolean readBoolean() {
            return readIo(input::readBoolean);
        }

        @Override
        public int readInt() {
            return readIo(input::readInt);
        }

        @Override
        public long readLong() {
            return readIo(input::readLong);
        }

        @Override
        public double readDouble() {
            return readIo(input::readDouble);
        }

        @Override
        public String readString(final int maxUtf8Bytes) {
            byte[] encoded = readByteArray(maxUtf8Bytes);
            try {
                return StandardCharsets.UTF_8
                        .newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(encoded))
                        .toString();
            } catch (CharacterCodingException exception) {
                throw new PortablePacketException("Malformed UTF-8 string in packet payload", exception);
            }
        }

        @Override
        public byte[] readByteArray(final int maxBytes) {
            int length = readInt();
            validateLimit("byte array", length, maxBytes);
            if (length < 0) {
                throw new PortablePacketException("Negative byte array length in packet payload: " + length);
            }
            byte[] value = new byte[length];
            readIo(() -> {
                input.readFully(value);
                return null;
            });
            return value;
        }

        @Override
        public PortableIdentifier readIdentifier() {
            String text = readString(IDENTIFIER_MAX_BYTES);
            int separator = text.indexOf(':');
            if (separator < 0) {
                throw new PortablePacketException("Identifier payload is missing namespace: " + text);
            }
            return PortableIdentifier.parse(text, text.substring(0, separator));
        }

        @Override
        public <T> List<T> readList(final int maxElements, final Function<PortablePacketReader, T> elementReader) {
            Objects.requireNonNull(elementReader, "elementReader");
            int size = readInt();
            validateLimit("collection", size, maxElements);
            if (size < 0) {
                throw new PortablePacketException("Negative collection size in packet payload: " + size);
            }
            List<T> values = new ArrayList<>(size);
            for (int index = 0; index < size; index++) {
                values.add(elementReader.apply(this));
            }
            return List.copyOf(values);
        }

        @Override
        public void requireFullyRead() {
            int unread = readIo(input::available);
            int consumed = payload.length - unread;
            if (unread != 0) {
                throw new PortablePacketException(
                        "Malformed packet payload: " + unread + " unread byte(s) remain after " + consumed + " byte(s)"
                );
            }
        }

        private <T> T readIo(final IoSupplier<T> supplier) {
            try {
                return supplier.get();
            } catch (EOFException exception) {
                throw new PortablePacketException("Truncated packet payload", exception);
            } catch (IOException exception) {
                throw new PortablePacketException("Failed to decode packet payload", exception);
            }
        }
    }

    private static void validateLimit(final String label, final int actual, final int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must not be negative for " + label + ": " + limit);
        }
        if (actual > limit) {
            throw new PortablePacketException(label + " length " + actual + " exceeds limit " + limit);
        }
    }

    @FunctionalInterface
    private interface IoRunnable {
        void run() throws IOException;
    }

    @FunctionalInterface
    private interface IoSupplier<T> {
        T get() throws IOException;
    }
}

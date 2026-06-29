/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.stability.InternalApi;
import dev.portablemc.api.stability.Since;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Stable wire envelope shared by native loader payload bridges.
 *
 * <p>The native loaders frame custom payloads differently. Portable API keeps
 * the packet id, direction, phase, protocol version, and bounded payload in a
 * small explicit envelope so every bridge rejects malformed or incompatible
 * packets before invoking user handlers.</p>
 */
@InternalApi
@Since("1.1.0")
public final class PortablePacketWireFormat {
    private static final int ENVELOPE_OVERHEAD_LIMIT = 2048;
    private static final int TEXT_LIMIT = 512;

    private PortablePacketWireFormat() {
    }

    /**
     * Encodes one portable packet for a native custom-payload body.
     *
     * @param packet encoded packet
     * @return wire bytes
     */
    public static byte[] encode(final PortableEncodedPacket packet) {
        Objects.requireNonNull(packet, "packet");
        byte[] payload = packet.payload();
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(bytes);
            writeText(output, packet.id().asString());
            writeText(output, packet.direction().name());
            writeText(output, packet.phase().name());
            output.writeInt(packet.protocolVersion());
            output.writeInt(payload.length);
            output.write(payload);
            return bytes.toByteArray();
        } catch (IOException exception) {
            throw new PortablePacketException("Failed to encode portable packet envelope", exception);
        }
    }

    /**
     * Decodes a native payload body and verifies it matches the registration.
     *
     * @param registration expected registration
     * @param wireBytes native payload bytes
     * @return encoded packet ready for dispatch
     */
    public static PortableEncodedPacket decode(
            final PortablePacketRegistration<?> registration,
            final byte[] wireBytes
    ) {
        Objects.requireNonNull(registration, "registration");
        PortablePacketType<?> type = registration.type();
        byte[] copy = Objects.requireNonNull(wireBytes, "wireBytes").clone();
        int maxWireBytes = type.maxPayloadBytes() + ENVELOPE_OVERHEAD_LIMIT;
        if (copy.length > maxWireBytes) {
            throw new PortablePacketException(
                    "Packet " + type.id() + " wire payload " + copy.length + " exceeds limit " + maxWireBytes
            );
        }
        try {
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(copy));
            PortableIdentifier id = PortableIdentifier.parse(readText(input), type.id().namespace());
            PacketDirection direction = PacketDirection.valueOf(readText(input));
            NetworkPhase phase = NetworkPhase.valueOf(readText(input));
            int protocolVersion = input.readInt();
            int payloadLength = input.readInt();
            if (payloadLength < 0) {
                throw new PortablePacketException("Negative packet payload length in envelope: " + payloadLength);
            }
            if (payloadLength > type.maxPayloadBytes()) {
                throw new PortablePacketException(
                        "Packet " + id + " payload " + payloadLength + " exceeds limit " + type.maxPayloadBytes()
                );
            }
            byte[] payload = new byte[payloadLength];
            input.readFully(payload);
            if (input.available() != 0) {
                throw new PortablePacketException("Trailing bytes after portable packet envelope");
            }
            return new PortableEncodedPacket(id, phase, direction, protocolVersion, payload);
        } catch (EOFException exception) {
            throw new PortablePacketException("Truncated portable packet envelope", exception);
        } catch (IllegalArgumentException exception) {
            throw new PortablePacketException("Invalid portable packet envelope enum value", exception);
        } catch (IOException exception) {
            throw new PortablePacketException("Failed to decode portable packet envelope", exception);
        }
    }

    /**
     * Reads the packet routing fields from an envelope without decoding the
     * typed payload body. Generic-channel loader bridges use this to find the
     * correct registration before applying its payload-size limit.
     *
     * @param wireBytes native payload bytes
     * @return envelope header
     */
    public static Header inspect(final byte[] wireBytes) {
        byte[] copy = Objects.requireNonNull(wireBytes, "wireBytes").clone();
        try {
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(copy));
            PortableIdentifier id = PortableIdentifier.parse(readText(input), "portable");
            PacketDirection direction = PacketDirection.valueOf(readText(input));
            NetworkPhase phase = NetworkPhase.valueOf(readText(input));
            int protocolVersion = input.readInt();
            int payloadLength = input.readInt();
            if (payloadLength < 0) {
                throw new PortablePacketException("Negative packet payload length in envelope: " + payloadLength);
            }
            return new Header(id, direction, phase, protocolVersion, payloadLength);
        } catch (EOFException exception) {
            throw new PortablePacketException("Truncated portable packet envelope", exception);
        } catch (IllegalArgumentException exception) {
            throw new PortablePacketException("Invalid portable packet envelope enum value", exception);
        } catch (IOException exception) {
            throw new PortablePacketException("Failed to inspect portable packet envelope", exception);
        }
    }

    /**
     * Routing fields from a portable packet envelope.
     *
     * @param id packet id
     * @param direction packet direction
     * @param phase packet phase
     * @param protocolVersion encoded protocol version
     * @param payloadLength encoded payload length
     */
    public record Header(
            PortableIdentifier id,
            PacketDirection direction,
            NetworkPhase phase,
            int protocolVersion,
            int payloadLength
    ) {
        /**
         * Validates a header.
         */
        public Header {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(direction, "direction");
            Objects.requireNonNull(phase, "phase");
            if (protocolVersion < 1) {
                throw new IllegalArgumentException("protocolVersion must be positive: " + protocolVersion);
            }
            if (payloadLength < 0) {
                throw new IllegalArgumentException("payloadLength must not be negative: " + payloadLength);
            }
        }
    }

    private static void writeText(final DataOutputStream output, final String value) throws IOException {
        byte[] bytes = Objects.requireNonNull(value, "value").getBytes(StandardCharsets.UTF_8);
        if (bytes.length > TEXT_LIMIT) {
            throw new PortablePacketException("Packet envelope text exceeds " + TEXT_LIMIT + " bytes");
        }
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    private static String readText(final DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0) {
            throw new PortablePacketException("Negative packet envelope text length: " + length);
        }
        if (length > TEXT_LIMIT) {
            throw new PortablePacketException("Packet envelope text exceeds " + TEXT_LIMIT + " bytes");
        }
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

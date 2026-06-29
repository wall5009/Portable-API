/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.PortableIdentifier;
import org.junit.jupiter.api.Test;

/**
 * Tests for the native-loader packet envelope.
 */
final class PortablePacketWireFormatTest {
    @Test
    void roundTripsEnvelope() {
        PortablePacketRegistration<TestPacket> registration = registration(PacketDirection.CLIENT_TO_SERVER, 64);
        PortableEncodedPacket encoded = new PortableEncodedPacket(
                registration.type().id(),
                registration.type().phase(),
                registration.type().direction(),
                registration.type().protocolVersion(),
                new byte[] {1, 2, 3}
        );

        PortableEncodedPacket decoded = PortablePacketWireFormat.decode(
                registration,
                PortablePacketWireFormat.encode(encoded)
        );

        assertEquals(encoded.id(), decoded.id());
        assertEquals(encoded.direction(), decoded.direction());
        assertEquals(encoded.phase(), decoded.phase());
        assertEquals(encoded.protocolVersion(), decoded.protocolVersion());
        assertArrayEquals(encoded.payload(), decoded.payload());
    }

    @Test
    void dispatchRejectsMismatchedEnvelopeFields() {
        PortablePacketRegistration<TestPacket> registration = registration(PacketDirection.CLIENT_TO_SERVER, 64);
        PortableEncodedPacket wrongDirection = new PortableEncodedPacket(
                registration.type().id(),
                registration.type().phase(),
                PacketDirection.SERVER_TO_CLIENT,
                registration.type().protocolVersion(),
                new byte[] {1}
        );

        assertThrows(PortablePacketException.class, () -> registration.dispatch(
                PortablePacketWireFormat.decode(registration, PortablePacketWireFormat.encode(wrongDirection)),
                new PortablePacketContext(PacketDirection.CLIENT_TO_SERVER, java.util.Optional.empty(), Runnable::run)
        ));

        PortableEncodedPacket wrongProtocol = new PortableEncodedPacket(
                registration.type().id(),
                registration.type().phase(),
                registration.type().direction(),
                registration.type().protocolVersion() + 1,
                new byte[] {1}
        );
        assertThrows(PortablePacketException.class, () -> registration.dispatch(
                PortablePacketWireFormat.decode(registration, PortablePacketWireFormat.encode(wrongProtocol)),
                new PortablePacketContext(PacketDirection.CLIENT_TO_SERVER, java.util.Optional.empty(), Runnable::run)
        ));
    }

    @Test
    void rejectsOversizedWirePayload() {
        PortablePacketRegistration<TestPacket> registration = registration(PacketDirection.CLIENT_TO_SERVER, 4);
        PortableEncodedPacket oversized = new PortableEncodedPacket(
                registration.type().id(),
                registration.type().phase(),
                registration.type().direction(),
                registration.type().protocolVersion(),
                new byte[] {1, 2, 3, 4, 5}
        );

        assertThrows(PortablePacketException.class, () ->
                PortablePacketWireFormat.decode(registration, PortablePacketWireFormat.encode(oversized)));
    }

    private static PortablePacketRegistration<TestPacket> registration(
            final PacketDirection direction,
            final int maxPayloadBytes
    ) {
        PortablePacketType<TestPacket> type = new PortablePacketType<>(
                PortableIdentifier.of("example", "wire"),
                NetworkPhase.PLAY,
                direction,
                3,
                maxPayloadBytes,
                new PortablePacketCodec<>() {
                    @Override
                    public void encode(final PortablePacketWriter writer, final TestPacket packet) {
                        writer.writeInt(packet.value());
                    }

                    @Override
                    public TestPacket decode(final PortablePacketReader reader) {
                        return new TestPacket(reader.readInt());
                    }
                }
        );
        return new PortablePacketRegistration<>(type, (packet, context) -> { });
    }

    private record TestPacket(int value) {
    }
}

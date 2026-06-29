/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.PortableIdentifier;
import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for bounded portable packet encoding and dispatch.
 */
final class PortablePacketBufferTest {
    private static final PortablePacketCodec<TestPacket> CODEC = new PortablePacketCodec<>() {
        @Override
        public void encode(final PortablePacketWriter writer, final TestPacket packet) {
            writer.writeBoolean(packet.enabled());
            writer.writeInt(packet.count());
            writer.writeLong(packet.seed());
            writer.writeDouble(packet.ratio());
            writer.writeString(packet.name(), 32);
            writer.writeByteArray(packet.payload(), 8);
            writer.writeIdentifier(packet.id());
            writer.writeCollection(packet.values(), 4, (out, value) -> out.writeInt(value));
        }

        @Override
        public TestPacket decode(final PortablePacketReader reader) {
            return new TestPacket(
                    reader.readBoolean(),
                    reader.readInt(),
                    reader.readLong(),
                    reader.readDouble(),
                    reader.readString(32),
                    reader.readByteArray(8),
                    reader.readIdentifier(),
                    reader.readList(4, PortablePacketReader::readInt)
            );
        }
    };

    @Test
    void roundTripsPrimitiveIdentifierBytesAndCollections() {
        PortablePacketType<TestPacket> type = packetType(256);
        TestPacket packet = new TestPacket(
                true,
                42,
                99L,
                0.5D,
                "hello",
                new byte[] {1, 2, 3},
                PortableIdentifier.of("example", "packet"),
                List.of(4, 5)
        );

        TestPacket decoded = PortablePacketBuffer.decode(type, PortablePacketBuffer.encode(type, packet));

        assertEquals(packet.enabled(), decoded.enabled());
        assertEquals(packet.count(), decoded.count());
        assertEquals(packet.seed(), decoded.seed());
        assertEquals(packet.ratio(), decoded.ratio());
        assertEquals(packet.name(), decoded.name());
        assertArrayEquals(packet.payload(), decoded.payload());
        assertEquals(packet.id(), decoded.id());
        assertEquals(packet.values(), decoded.values());
    }

    @Test
    void rejectsOversizedPayloadAndTrailingBytes() {
        PortablePacketType<TestPacket> type = packetType(8);
        TestPacket packet = new TestPacket(true, 1, 2L, 3.0D, "too large", new byte[0],
                PortableIdentifier.of("example", "packet"), List.of());

        assertThrows(PortablePacketException.class, () -> PortablePacketBuffer.encode(type, packet));

        PortablePacketType<Integer> intType = new PortablePacketType<>(
                PortableIdentifier.of("example", "int"),
                NetworkPhase.PLAY,
                PacketDirection.CLIENT_TO_SERVER,
                1,
                16,
                new PortablePacketCodec<>() {
                    @Override
                    public void encode(final PortablePacketWriter writer, final Integer value) {
                        writer.writeInt(value);
                        writer.writeInt(123);
                    }

                    @Override
                    public Integer decode(final PortablePacketReader reader) {
                        return reader.readInt();
                    }
                }
        );

        assertThrows(PortablePacketException.class, () -> PortablePacketBuffer.decode(intType, PortablePacketBuffer.encode(intType, 1)));
    }

    @Test
    void registersDispatchesAndRejectsProtocolMismatch() {
        FakeNetworkingAdapter adapter = new FakeNetworkingAdapter();
        PortableNetworking networking = new PortableNetworking("example", adapter);
        PortablePacketType<TestPacket> type = networking.packetType(
                "test",
                NetworkPhase.PLAY,
                PacketDirection.CLIENT_TO_SERVER,
                7,
                256,
                CODEC
        );
        List<TestPacket> handled = new ArrayList<>();
        networking.registerClientToServer(type, (packet, context) -> handled.add(packet));

        TestPacket packet = new TestPacket(false, 1, 2L, 3.0D, "ok", new byte[] {4},
                PortableIdentifier.of("example", "value"), List.of(6));
        PortableEncodedPacket encoded = networking.encode(type, packet);
        adapter.registrations().get(0).dispatch(encoded, context());

        assertEquals(1, handled.size());
        assertThrows(IllegalStateException.class, () -> networking.registerClientToServer(type, (ignored, context) -> { }));
        assertThrows(PortablePacketException.class, () -> adapter.registrations().get(0).dispatch(
                new PortableEncodedPacket(type.id(), type.phase(), type.direction(), 8, encoded.payload()),
                context()
        ));
    }

    @Test
    void sendsClientToServerThroughAdapter() {
        FakeNetworkingAdapter adapter = new FakeNetworkingAdapter();
        PortableNetworking networking = new PortableNetworking("example", adapter);
        PortablePacketType<TestPacket> type = networking.packetType(
                "send",
                NetworkPhase.PLAY,
                PacketDirection.CLIENT_TO_SERVER,
                1,
                256,
                CODEC
        );
        TestPacket packet = new TestPacket(false, 1, 2L, 3.0D, "ok", new byte[0],
                PortableIdentifier.of("example", "value"), List.of());

        networking.sendToServer(type, packet);

        assertEquals(type.id(), adapter.sentToServer().get(0).id());
    }

    private static PortablePacketContext context() {
        return new PortablePacketContext(PacketDirection.CLIENT_TO_SERVER, Optional.empty(), Runnable::run);
    }

    private static PortablePacketType<TestPacket> packetType(final int maxPayload) {
        return new PortablePacketType<>(
                PortableIdentifier.of("example", "test"),
                NetworkPhase.PLAY,
                PacketDirection.CLIENT_TO_SERVER,
                1,
                maxPayload,
                CODEC
        );
    }

    private record TestPacket(
            boolean enabled,
            int count,
            long seed,
            double ratio,
            String name,
            byte[] payload,
            PortableIdentifier id,
            List<Integer> values
    ) {
    }

    private static final class FakeNetworkingAdapter implements PortableNetworkingAdapter {
        private final List<PortablePacketRegistration<?>> registrations = new ArrayList<>();
        private final List<PortableEncodedPacket> sentToServer = new ArrayList<>();

        @Override
        public void declare(final PortableNetworkChannel channel) {
        }

        @Override
        public <T> void registerPacket(final PortablePacketRegistration<T> registration) {
            registrations.add(registration);
        }

        @Override
        public void sendToServer(final PortableEncodedPacket packet) {
            sentToServer.add(packet);
        }

        private List<PortablePacketRegistration<?>> registrations() {
            return registrations;
        }

        private List<PortableEncodedPacket> sentToServer() {
            return sentToServer;
        }
    }
}

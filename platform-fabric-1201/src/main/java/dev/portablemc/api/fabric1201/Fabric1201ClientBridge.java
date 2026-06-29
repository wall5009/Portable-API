/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.fabric1201;

import dev.portablemc.api.internal.DefaultPortableModContext;
import dev.portablemc.api.mc1201.Minecraft1201Adapters;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortablePacketContext;
import dev.portablemc.api.network.PortablePacketException;
import dev.portablemc.api.network.PortablePacketRegistration;
import dev.portablemc.api.network.PortablePacketWireFormat;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Client-only Fabric 1.20.1 bridge for Portable API networking and lifecycle.
 */
final class Fabric1201ClientBridge {
    private Fabric1201ClientBridge() {
    }

    static void install(
            final DefaultPortableModContext context,
            final Fabric1201Bootstrap.FabricRuntime runtime
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(runtime, "runtime");
        ClientTickEvents.END_CLIENT_TICK.register(client -> context.lifecycle().fireClientTick());
        for (PortablePacketRegistration<?> registration : runtime.packetRegistrations()) {
            if (registration.type().direction() == PacketDirection.SERVER_TO_CLIENT) {
                registerClientReceiver(context, registration);
            }
        }
    }

    static void sendToServer(final PortableEncodedPacket packet) {
        if (packet.direction() != PacketDirection.CLIENT_TO_SERVER) {
            throw new IllegalArgumentException("Packet " + packet.id() + " is not client-to-server");
        }
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeBytes(PortablePacketWireFormat.encode(packet));
        ClientPlayNetworking.send(Minecraft1201Adapters.resourceLocation(packet.id()), buffer);
    }

    private static <T> void registerClientReceiver(
            final DefaultPortableModContext context,
            final PortablePacketRegistration<T> registration
    ) {
        boolean registered = ClientPlayNetworking.registerGlobalReceiver(
                Minecraft1201Adapters.resourceLocation(registration.type().id()),
                (client, handler, buffer, responseSender) -> {
                    byte[] wireBytes = readWireBytes(buffer, registration);
                    client.execute(() -> {
                        try {
                            PortableEncodedPacket encoded = PortablePacketWireFormat.decode(registration, wireBytes);
                            registration.dispatch(encoded, new PortablePacketContext(
                                    PacketDirection.SERVER_TO_CLIENT,
                                    Optional.empty(),
                                    client::execute
                            ));
                        } catch (PortablePacketException exception) {
                            context.logger().warn("Rejected malformed portable packet " + registration.type().id());
                        }
                    });
                }
        );
        if (!registered) {
            throw new IllegalStateException("Duplicate Fabric client packet receiver for " + registration.type().id());
        }
    }

    private static byte[] readWireBytes(
            final FriendlyByteBuf buffer,
            final PortablePacketRegistration<?> registration
    ) {
        int readableBytes = buffer.readableBytes();
        int maxWireBytes = registration.type().maxPayloadBytes() + 2048;
        if (readableBytes > maxWireBytes) {
            throw new PortablePacketException(
                    "Packet " + registration.type().id() + " wire payload " + readableBytes
                            + " exceeds limit " + maxWireBytes
            );
        }
        byte[] bytes = new byte[readableBytes];
        buffer.readBytes(bytes);
        return bytes;
    }
}

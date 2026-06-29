/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.fabric1211;

import dev.portablemc.api.internal.DefaultPortableModContext;
import dev.portablemc.api.mc1211.Minecraft1211Adapters;
import dev.portablemc.api.network.PacketDirection;
import dev.portablemc.api.network.PortableEncodedPacket;
import dev.portablemc.api.network.PortablePacketContext;
import dev.portablemc.api.network.PortablePacketException;
import dev.portablemc.api.network.PortablePacketRegistration;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client-only Fabric 1.21.1 bridge for Portable API networking and lifecycle.
 */
final class Fabric1211ClientBridge {
    private Fabric1211ClientBridge() {
    }

    static void install(
            final DefaultPortableModContext context,
            final Fabric1211Bootstrap.FabricRuntime runtime
    ) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(runtime, "runtime");
        ClientTickEvents.END_CLIENT_TICK.register(client -> context.lifecycle().fireClientTick());
        for (PortablePacketRegistration<?> registration : runtime.packetRegistrations()) {
            if (registration.type().direction() == PacketDirection.SERVER_TO_CLIENT) {
                registerClientReceiver(context, registration, runtime.payloadType(registration));
            }
        }
    }

    static void sendToServer(final PortableEncodedPacket packet) {
        if (packet.direction() != PacketDirection.CLIENT_TO_SERVER) {
            throw new IllegalArgumentException("Packet " + packet.id() + " is not client-to-server");
        }
        CustomPacketPayload.Type<Fabric1211PortablePayload> payloadType =
                new CustomPacketPayload.Type<>(Minecraft1211Adapters.resourceLocation(packet.id()));
        ClientPlayNetworking.send(new Fabric1211PortablePayload(payloadType, packet));
    }

    private static <T> void registerClientReceiver(
            final DefaultPortableModContext context,
            final PortablePacketRegistration<T> registration,
            final CustomPacketPayload.Type<Fabric1211PortablePayload> payloadType
    ) {
        if (payloadType == null) {
            throw new IllegalStateException("No Fabric payload type registered for " + registration.type().id());
        }
        boolean registered = ClientPlayNetworking.registerGlobalReceiver(payloadType, (payload, networkContext) -> {
            try {
                registration.dispatch(payload.packet(), new PortablePacketContext(
                        PacketDirection.SERVER_TO_CLIENT,
                        Optional.empty(),
                        networkContext.client()::execute
                ));
            } catch (PortablePacketException exception) {
                context.logger().warn("Rejected malformed portable packet " + registration.type().id());
            }
        });
        if (!registered) {
            throw new IllegalStateException("Duplicate Fabric client packet receiver for " + registration.type().id());
        }
    }
}

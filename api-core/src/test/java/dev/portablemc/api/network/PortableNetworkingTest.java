/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.spi.PortableNetworkingAdapter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Contract tests for network channel declaration.
 */
final class PortableNetworkingTest {
    @Test
    void declaresChannelThroughAdapter() {
        List<PortableNetworkChannel> channels = new ArrayList<>();
        PortableNetworking networking = new PortableNetworking("example", channels::add);

        PortableNetworkChannel channel = networking.declareChannel("main", NetworkPhase.PLAY, 1, 32767);

        assertEquals(channel, channels.get(0));
        assertEquals("example:main", channel.id().asString());
    }

    @Test
    void rejectsDuplicateChannel() {
        PortableNetworkingAdapter adapter = channel -> {
        };
        PortableNetworking networking = new PortableNetworking("example", adapter);

        networking.declareChannel("main", NetworkPhase.PLAY, 1, 32767);

        assertThrows(IllegalStateException.class, () -> networking.declareChannel("main", NetworkPhase.PLAY, 1, 32767));
    }
}

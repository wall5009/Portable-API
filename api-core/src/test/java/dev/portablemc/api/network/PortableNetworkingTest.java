/*
 * MIT License
 *
 * Copyright (c) 2026 PortableMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

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
package dev.portablemc.api;

/**
 * Minecraft versions covered by the V1 compatibility contract.
 */
public enum MinecraftVersion {
    /** Minecraft 1.20.1, compiled with Java 17. */
    MC_1_20_1("1.20.1", 17),

    /** Minecraft 1.21.1, compiled with Java 21 in this repository. */
    MC_1_21_1("1.21.1", 21);

    private final String id;
    private final int javaRelease;

    MinecraftVersion(final String id, final int javaRelease) {
        this.id = id;
        this.javaRelease = javaRelease;
    }

    /**
     * Returns Mojang's release identifier, for example {@code 1.20.1}.
     *
     * @return stable Minecraft version string
     */
    public String id() {
        return id;
    }

    /**
     * Returns the Java release used to compile target-specific modules.
     *
     * @return Java language/runtime release
     */
    public int javaRelease() {
        return javaRelease;
    }
}

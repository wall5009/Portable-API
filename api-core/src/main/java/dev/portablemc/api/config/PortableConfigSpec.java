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
package dev.portablemc.api.config;

import java.util.Objects;

/**
 * Simple configuration file declaration.
 *
 * @param fileName file name below the loader config directory
 * @param defaultContent default UTF-8 content written when the file is missing
 */
public record PortableConfigSpec(String fileName, String defaultContent) {
    /**
     * Validates a config declaration.
     */
    public PortableConfigSpec {
        Objects.requireNonNull(fileName, "fileName");
        Objects.requireNonNull(defaultContent, "defaultContent");
        if (fileName.contains("/") || fileName.contains("\\") || fileName.contains("..")) {
            throw new IllegalArgumentException("Config fileName must be a simple file name: " + fileName);
        }
    }
}

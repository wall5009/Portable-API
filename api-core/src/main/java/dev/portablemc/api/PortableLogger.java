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
 * Small logging facade used by shared code.
 *
 * <p>Loader modules adapt this to SLF4J because both Fabric and Forge-family
 * loaders expose SLF4J in development and production. The common API does not
 * depend on SLF4J directly so unit tests and build-time tools can use the core
 * module without inheriting a logging binding.</p>
 */
public interface PortableLogger {
    /**
     * Logs diagnostic information useful during development.
     *
     * @param message message template or literal
     */
    void debug(String message);

    /**
     * Logs normal lifecycle information.
     *
     * @param message message template or literal
     */
    void info(String message);

    /**
     * Logs a recoverable problem.
     *
     * @param message message template or literal
     */
    void warn(String message);

    /**
     * Logs a failure with its cause.
     *
     * @param message message template or literal
     * @param throwable failure cause
     */
    void error(String message, Throwable throwable);
}

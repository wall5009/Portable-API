/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
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

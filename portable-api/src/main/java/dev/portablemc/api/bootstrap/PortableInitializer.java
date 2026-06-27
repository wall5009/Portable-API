package dev.portablemc.api.bootstrap;

/**
 * Consumer mod initialization callback.
 *
 * <p>Implementations should register content, lifecycle callbacks, commands, packets, and resources
 * through the supplied {@link ModContext}. Implementations must not assume a specific loader or
 * game version.
 */
@FunctionalInterface
public interface PortableInitializer {
  /** Initializes a mod using loader-neutral services. */
  void initialize(ModContext context);
}

package dev.portablemc.api;

import dev.portablemc.api.bootstrap.ModContext;
import dev.portablemc.api.bootstrap.PortableInitializer;
import dev.portablemc.api.runtime.PortableRuntime;
import dev.portablemc.internal.bootstrap.PortableBootstrap;
import java.util.Objects;

/**
 * Static entry point for consumers of Portable API.
 *
 * <p>Loader modules install the runtime during their own bootstrap phase. Consumer mods should call
 * {@link #initialize(String, PortableInitializer)} from their loader entry point and keep all
 * regular mod setup inside the provided loader-neutral {@link ModContext}.
 */
public final class PortableApi {
  private PortableApi() {}

  /**
   * Returns the installed runtime.
   *
   * @throws IllegalStateException when Portable API has not been installed by a platform module
   */
  public static PortableRuntime get() {
    return PortableBootstrap.runtime();
  }

  /** Returns whether a platform module has installed the runtime. */
  public static boolean isAvailable() {
    return PortableBootstrap.isInstalled();
  }

  /**
   * Initializes a consumer mod against the installed Portable API runtime.
   *
   * @param modId the owning mod id
   * @param initializer callback invoked exactly once by the caller
   */
  public static void initialize(String modId, PortableInitializer initializer) {
    Objects.requireNonNull(modId, "modId");
    Objects.requireNonNull(initializer, "initializer");
    PortableBootstrap.runtime().initialize(modId, initializer);
  }
}

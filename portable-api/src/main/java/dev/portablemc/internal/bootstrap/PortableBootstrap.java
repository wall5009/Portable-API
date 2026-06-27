package dev.portablemc.internal.bootstrap;

import dev.portablemc.api.runtime.PortableRuntime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/** Internal runtime installer used only by Portable API platform modules. */
public final class PortableBootstrap {
  private static final AtomicReference<PortableRuntime> RUNTIME = new AtomicReference<>();

  private PortableBootstrap() {}

  /** Installs the runtime once. */
  public static void install(PortableRuntime runtime) {
    Objects.requireNonNull(runtime, "runtime");
    if (!RUNTIME.compareAndSet(null, runtime)) {
      throw new IllegalStateException("Portable API runtime is already installed");
    }
  }

  /** Returns whether a runtime has been installed. */
  public static boolean isInstalled() {
    return RUNTIME.get() != null;
  }

  /** Returns the installed runtime. */
  public static PortableRuntime runtime() {
    PortableRuntime runtime = RUNTIME.get();
    if (runtime == null) {
      throw new IllegalStateException("Portable API runtime has not been installed");
    }
    return runtime;
  }

  /** Test-only reset hook intentionally left in an internal package. */
  public static void resetForTesting() {
    RUNTIME.set(null);
  }
}

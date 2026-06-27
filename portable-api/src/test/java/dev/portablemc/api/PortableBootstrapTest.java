package dev.portablemc.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.portablemc.api.bootstrap.PortableInitializer;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.api.runtime.PlatformInfo;
import dev.portablemc.api.runtime.PortableRuntime;
import dev.portablemc.internal.bootstrap.PortableBootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

final class PortableBootstrapTest {
  @AfterEach
  void reset() {
    PortableBootstrap.resetForTesting();
  }

  @Test
  void installsRuntimeOnce() {
    PortableRuntime runtime = new FakeRuntime();

    assertFalse(PortableApi.isAvailable());
    PortableBootstrap.install(runtime);

    assertTrue(PortableApi.isAvailable());
    assertSame(runtime, PortableApi.get());
    assertThrows(IllegalStateException.class, () -> PortableBootstrap.install(new FakeRuntime()));
  }

  private static final class FakeRuntime implements PortableRuntime {
    @Override
    public PlatformInfo platform() {
      throw new UnsupportedOperationException();
    }

    @Override
    public CapabilityReport capabilities() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void initialize(String modId, PortableInitializer initializer) {
      throw new UnsupportedOperationException();
    }
  }
}

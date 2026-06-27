package dev.portablemc.internal.platform.fabric1201;

import dev.portablemc.internal.bootstrap.PortableBootstrap;
import dev.portablemc.internal.core.CorePortableRuntime;
import net.fabricmc.api.ClientModInitializer;

/** Fabric 1.20.1 client bootstrap. */
public final class PortableFabric1201Client implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    if (PortableBootstrap.runtime() instanceof CorePortableRuntime runtime) {
      runtime.fireClientSetup();
    }
  }
}

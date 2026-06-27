package dev.portablemc.validation.fabric1211;

import dev.portablemc.api.PortableApi;
import net.fabricmc.api.ModInitializer;

/** Blank validation consumer for Fabric 1.21.1. */
public final class PortableValidationFabric1211 implements ModInitializer {
  @Override
  public void onInitialize() {
    PortableApi.initialize("portable_api_validation", context -> {});
  }
}

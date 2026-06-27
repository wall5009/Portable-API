package dev.portablemc.validation.fabric1201;

import dev.portablemc.api.PortableApi;
import net.fabricmc.api.ModInitializer;

/** Blank validation consumer for Fabric 1.20.1. */
public final class PortableValidationFabric1201 implements ModInitializer {
  @Override
  public void onInitialize() {
    PortableApi.initialize("portable_api_validation", context -> {});
  }
}

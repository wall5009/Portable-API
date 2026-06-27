package dev.portablemc.validation.forge1201;

import dev.portablemc.api.PortableApi;
import net.minecraftforge.fml.common.Mod;

/** Blank validation consumer for Forge 1.20.1. */
@Mod("portable_api_validation")
public final class PortableValidationForge1201 {
  public PortableValidationForge1201() {
    PortableApi.initialize("portable_api_validation", context -> {});
  }
}

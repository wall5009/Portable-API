package dev.portablemc.validation.neoforge1211;

import dev.portablemc.api.PortableApi;
import net.neoforged.fml.common.Mod;

/** Blank validation consumer for NeoForge 1.21.1. */
@Mod("portable_api_validation")
public final class PortableValidationNeoForge1211 {
  public PortableValidationNeoForge1211() {
    PortableApi.initialize("portable_api_validation", context -> {});
  }
}

package dev.portablemc.internal.core.registry;

import dev.portablemc.api.registry.DeferredRegistry;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistrationService;
import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.internal.core.TargetBridge;
import java.util.Objects;

/** Default registration service. */
public final class DefaultRegistrationService implements RegistrationService {
  private final CapabilityReport capabilities;
  private final TargetBridge targetBridge;

  public DefaultRegistrationService(CapabilityReport capabilities, TargetBridge targetBridge) {
    this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
    this.targetBridge = Objects.requireNonNull(targetBridge, "targetBridge");
  }

  @Override
  public <T> DeferredRegistry<T> create(Identifier registryId, Class<T> valueType) {
    capabilities.require(Capability.REGISTRY_SUPPLIERS);
    return new DefaultDeferredRegistry<>(registryId, valueType, targetBridge);
  }
}

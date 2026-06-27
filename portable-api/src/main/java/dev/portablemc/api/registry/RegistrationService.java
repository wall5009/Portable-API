package dev.portablemc.api.registry;

/** Registry creation and lookup service. */
public interface RegistrationService {
  /** Creates a deferred registry facade for the supplied registry id. */
  <T> DeferredRegistry<T> create(Identifier registryId, Class<T> valueType);

  /** Creates a registry key without registering an object. */
  default <T> RegistryKey<T> key(Identifier registryId, Identifier valueId, Class<T> valueType) {
    return RegistryKey.of(registryId, valueId, valueType);
  }
}

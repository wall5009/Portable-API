package dev.portablemc.api.runtime;

import java.util.Map;
import java.util.Optional;

/** Immutable report describing which Portable API features are usable on the active target. */
public interface CapabilityReport {
  /** Returns the status for a capability. */
  CapabilityStatus status(Capability capability);

  /** Returns a human-readable reason for a limited, deferred, or unsupported capability. */
  Optional<String> note(Capability capability);

  /** Returns whether a capability is fully supported. */
  default boolean supports(Capability capability) {
    return status(capability) == CapabilityStatus.SUPPORTED;
  }

  /** Throws {@link UnsupportedFeatureException} unless the capability is fully supported. */
  default void require(Capability capability) {
    if (!supports(capability)) {
      throw new UnsupportedFeatureException(
          capability, status(capability), note(capability).orElse(""));
    }
  }

  /** Returns all known capability statuses. */
  Map<Capability, CapabilityStatus> asMap();
}

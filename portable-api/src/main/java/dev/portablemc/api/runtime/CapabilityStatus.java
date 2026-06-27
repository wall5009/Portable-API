package dev.portablemc.api.runtime;

/** Support status for a {@link Capability}. */
public enum CapabilityStatus {
  /** The feature is implemented for the active target. */
  SUPPORTED,

  /** The feature is present but intentionally exposes a reduced stable surface in v1. */
  LIMITED,

  /** The feature is intentionally deferred and calls should fail gracefully. */
  DEFERRED,

  /** The feature cannot be implemented safely for the active target. */
  UNSUPPORTED
}

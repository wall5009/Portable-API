package dev.portablemc.api.runtime;

/** Exception thrown when a v1 API surface is called on a target that cannot support it. */
public final class UnsupportedFeatureException extends UnsupportedOperationException {
  private final Capability capability;
  private final CapabilityStatus status;

  public UnsupportedFeatureException(Capability capability, CapabilityStatus status, String note) {
    super(message(capability, status, note));
    this.capability = capability;
    this.status = status;
  }

  /** Returns the requested capability. */
  public Capability capability() {
    return capability;
  }

  /** Returns the current capability status. */
  public CapabilityStatus status() {
    return status;
  }

  private static String message(Capability capability, CapabilityStatus status, String note) {
    String suffix = note == null || note.isBlank() ? "" : ": " + note;
    return "Portable API capability " + capability + " is " + status + suffix;
  }
}

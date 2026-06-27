package dev.portablemc.api.runtime;

/** Physical runtime side. */
public enum PhysicalSide {
  CLIENT,
  DEDICATED_SERVER;

  /** Returns whether client-only classes are expected to be present. */
  public boolean isClient() {
    return this == CLIENT;
  }
}

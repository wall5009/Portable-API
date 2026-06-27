package dev.portablemc.api.runtime;

/** Supported loader families. */
public enum LoaderKind {
  FABRIC("fabric"),
  FORGE("forge"),
  NEOFORGE("neoforge");

  private final String id;

  LoaderKind(String id) {
    this.id = id;
  }

  /** Returns the stable lowercase loader id. */
  public String id() {
    return id;
  }
}

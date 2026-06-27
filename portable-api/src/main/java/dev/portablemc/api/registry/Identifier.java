package dev.portablemc.api.registry;

import java.util.Objects;
import java.util.regex.Pattern;

/** Stable loader-neutral resource identifier of the form {@code namespace:path}. */
public record Identifier(String namespace, String path) {
  private static final Pattern NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
  private static final Pattern PATH = Pattern.compile("[a-z0-9_./-]+");

  public Identifier {
    namespace = Objects.requireNonNull(namespace, "namespace");
    path = Objects.requireNonNull(path, "path");
    if (!NAMESPACE.matcher(namespace).matches()) {
      throw new IllegalArgumentException("Invalid identifier namespace: " + namespace);
    }
    if (!PATH.matcher(path).matches()) {
      throw new IllegalArgumentException("Invalid identifier path: " + path);
    }
  }

  /** Parses {@code namespace:path}. */
  public static Identifier parse(String value) {
    Objects.requireNonNull(value, "value");
    int separator = value.indexOf(':');
    if (separator <= 0
        || separator == value.length() - 1
        || value.indexOf(':', separator + 1) >= 0) {
      throw new IllegalArgumentException("Identifier must be namespace:path: " + value);
    }
    return new Identifier(value.substring(0, separator), value.substring(separator + 1));
  }

  @Override
  public String toString() {
    return namespace + ":" + path;
  }
}

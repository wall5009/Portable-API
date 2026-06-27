package dev.portablemc.api.config;

import java.util.Objects;

/** Typed config value declaration. */
public record ConfigValue<T>(String key, Class<T> type, T defaultValue, String description) {
  public ConfigValue {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(type, "type");
    description = description == null ? "" : description;
  }
}

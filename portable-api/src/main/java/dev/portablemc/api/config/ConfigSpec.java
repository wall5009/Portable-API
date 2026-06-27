package dev.portablemc.api.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Immutable config declaration. */
public final class ConfigSpec {
  private final String name;
  private final ConfigScope scope;
  private final Map<String, ConfigValue<?>> values;

  private ConfigSpec(String name, ConfigScope scope, Map<String, ConfigValue<?>> values) {
    this.name = name;
    this.scope = scope;
    this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
  }

  /** Returns a new builder. */
  public static Builder builder(String name, ConfigScope scope) {
    return new Builder(name, scope);
  }

  public String name() {
    return name;
  }

  public ConfigScope scope() {
    return scope;
  }

  public Map<String, ConfigValue<?>> values() {
    return values;
  }

  /** Mutable builder for {@link ConfigSpec}. */
  public static final class Builder {
    private final String name;
    private final ConfigScope scope;
    private final Map<String, ConfigValue<?>> values = new LinkedHashMap<>();

    private Builder(String name, ConfigScope scope) {
      this.name = Objects.requireNonNull(name, "name");
      this.scope = Objects.requireNonNull(scope, "scope");
    }

    public <T> Builder value(String key, Class<T> type, T defaultValue, String description) {
      values.put(key, new ConfigValue<>(key, type, defaultValue, description));
      return this;
    }

    public ConfigSpec build() {
      return new ConfigSpec(name, scope, values);
    }
  }
}

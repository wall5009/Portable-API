package dev.portablemc.internal.core.config;

import dev.portablemc.api.config.ConfigService;
import dev.portablemc.api.config.ConfigSpec;
import dev.portablemc.api.config.ConfigValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/** Small properties-backed config service suitable for v1 primitives. */
public final class PropertiesConfigService implements ConfigService {
  private final String modId;
  private final Path configDirectory;

  public PropertiesConfigService(String modId, Path configDirectory) {
    this.modId = Objects.requireNonNull(modId, "modId");
    this.configDirectory = Objects.requireNonNull(configDirectory, "configDirectory");
  }

  @Override
  public LoadedConfig register(ConfigSpec spec) {
    Objects.requireNonNull(spec, "spec");
    Properties properties = new Properties();
    Path file = configDirectory.resolve(modId + "-" + spec.name() + ".properties");
    try {
      Files.createDirectories(configDirectory);
      if (Files.isRegularFile(file)) {
        try (InputStream input = Files.newInputStream(file)) {
          properties.load(input);
        }
      }
      boolean changed = false;
      for (ConfigValue<?> value : spec.values().values()) {
        if (!properties.containsKey(value.key())) {
          properties.setProperty(value.key(), String.valueOf(value.defaultValue()));
          changed = true;
        }
      }
      if (changed || !Files.isRegularFile(file)) {
        try (OutputStream output = Files.newOutputStream(file)) {
          properties.store(output, "Portable API config: " + modId + "/" + spec.name());
        }
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to load config " + file, exception);
    }
    return new Loaded(spec, properties);
  }

  private record Loaded(ConfigSpec spec, Properties properties) implements LoadedConfig {
    @Override
    public <T> T get(String key, Class<T> type) {
      ConfigValue<?> declaration = spec.values().get(key);
      if (declaration == null) {
        throw new IllegalArgumentException("Unknown config key: " + key);
      }
      String value = properties.getProperty(key, String.valueOf(declaration.defaultValue()));
      Object converted = convert(value, type);
      return type.cast(converted);
    }

    private static Object convert(String value, Class<?> type) {
      if (type == String.class) {
        return value;
      }
      if (type == Boolean.class || type == boolean.class) {
        return Boolean.parseBoolean(value);
      }
      if (type == Integer.class || type == int.class) {
        return Integer.parseInt(value);
      }
      if (type == Long.class || type == long.class) {
        return Long.parseLong(value);
      }
      if (type == Double.class || type == double.class) {
        return Double.parseDouble(value);
      }
      throw new IllegalArgumentException("Unsupported config value type: " + type.getName());
    }
  }
}

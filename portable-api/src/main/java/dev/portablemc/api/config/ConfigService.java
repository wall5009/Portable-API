package dev.portablemc.api.config;

/** Config registration service. */
public interface ConfigService {
  /** Registers and loads a config spec. */
  LoadedConfig register(ConfigSpec spec);

  /** Loaded config values. */
  interface LoadedConfig {
    /** Returns the declaration that created this loaded config. */
    ConfigSpec spec();

    /** Returns a typed config value, or the declared default when no persisted value exists. */
    <T> T get(String key, Class<T> type);
  }
}

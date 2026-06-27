package dev.portablemc.internal.core;

import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.CapabilityReport;
import dev.portablemc.api.runtime.CapabilityStatus;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/** Immutable capability report implementation. */
public final class DefaultCapabilityReport implements CapabilityReport {
  private final Map<Capability, CapabilityStatus> statuses;
  private final Map<Capability, String> notes;

  private DefaultCapabilityReport(
      Map<Capability, CapabilityStatus> statuses, Map<Capability, String> notes) {
    this.statuses = Collections.unmodifiableMap(new EnumMap<>(statuses));
    this.notes = Collections.unmodifiableMap(new EnumMap<>(notes));
  }

  /** Returns a builder with every capability initially marked unsupported. */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public CapabilityStatus status(Capability capability) {
    return statuses.getOrDefault(capability, CapabilityStatus.UNSUPPORTED);
  }

  @Override
  public Optional<String> note(Capability capability) {
    return Optional.ofNullable(notes.get(capability)).filter(note -> !note.isBlank());
  }

  @Override
  public Map<Capability, CapabilityStatus> asMap() {
    return statuses;
  }

  /** Mutable builder. */
  public static final class Builder {
    private final Map<Capability, CapabilityStatus> statuses = new EnumMap<>(Capability.class);
    private final Map<Capability, String> notes = new EnumMap<>(Capability.class);

    private Builder() {
      for (Capability capability : Capability.values()) {
        statuses.put(capability, CapabilityStatus.UNSUPPORTED);
      }
    }

    public Builder supported(Capability capability) {
      statuses.put(capability, CapabilityStatus.SUPPORTED);
      return this;
    }

    public Builder limited(Capability capability, String note) {
      statuses.put(capability, CapabilityStatus.LIMITED);
      notes.put(capability, note);
      return this;
    }

    public Builder deferred(Capability capability, String note) {
      statuses.put(capability, CapabilityStatus.DEFERRED);
      notes.put(capability, note);
      return this;
    }

    public Builder unsupported(Capability capability, String note) {
      statuses.put(capability, CapabilityStatus.UNSUPPORTED);
      notes.put(capability, note);
      return this;
    }

    public DefaultCapabilityReport build() {
      return new DefaultCapabilityReport(statuses, notes);
    }
  }
}

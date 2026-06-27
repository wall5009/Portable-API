package dev.portablemc.api.attachment;

import dev.portablemc.api.registry.Identifier;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Stable declaration for data attached to game objects. Persistence is intentionally deferred in
 * v1.
 */
public record AttachmentType<T>(Identifier id, Class<T> valueType, Supplier<T> defaultFactory) {
  public AttachmentType {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(valueType, "valueType");
    Objects.requireNonNull(defaultFactory, "defaultFactory");
  }
}

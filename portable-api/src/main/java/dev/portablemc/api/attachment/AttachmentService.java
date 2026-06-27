package dev.portablemc.api.attachment;

import dev.portablemc.api.registry.Identifier;
import java.util.function.Supplier;

/** Data attachment/component declaration service. */
public interface AttachmentService {
  /** Registers an attachment type. Persistent backing is target-dependent and deferred in v1. */
  <T> AttachmentType<T> register(Identifier id, Class<T> valueType, Supplier<T> defaultFactory);
}

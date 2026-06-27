package dev.portablemc.internal.core.attachment;

import dev.portablemc.api.attachment.AttachmentService;
import dev.portablemc.api.attachment.AttachmentType;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.CapabilityReport;
import java.util.Objects;
import java.util.function.Supplier;

/** V1 attachment declaration service. Persistent storage is intentionally deferred. */
public final class DefaultAttachmentService implements AttachmentService {
  private final CapabilityReport capabilities;

  public DefaultAttachmentService(CapabilityReport capabilities) {
    this.capabilities = Objects.requireNonNull(capabilities, "capabilities");
  }

  @Override
  public <T> AttachmentType<T> register(
      Identifier id, Class<T> valueType, Supplier<T> defaultFactory) {
    capabilities.require(Capability.DATA_ATTACHMENTS);
    return new AttachmentType<>(id, valueType, defaultFactory);
  }
}

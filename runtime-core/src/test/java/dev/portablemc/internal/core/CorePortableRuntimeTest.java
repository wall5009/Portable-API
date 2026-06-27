package dev.portablemc.internal.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.portablemc.api.command.CommandResult;
import dev.portablemc.api.command.CommandSpec;
import dev.portablemc.api.registry.Identifier;
import dev.portablemc.api.registry.RegistryKey;
import dev.portablemc.api.runtime.Capability;
import dev.portablemc.api.runtime.LoaderKind;
import dev.portablemc.api.runtime.PhysicalSide;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

final class CorePortableRuntimeTest {
  @Test
  void initializesContextAndStoresCommandDeclarations() {
    CorePortableRuntime runtime =
        new CorePortableRuntime(
            new DefaultPlatformInfo(
                LoaderKind.FABRIC,
                "1.20.1",
                Optional.of("loader"),
                PhysicalSide.CLIENT,
                false,
                Set.of("portable_api")),
            DefaultCapabilityReport.builder()
                .supported(Capability.COMMANDS)
                .supported(Capability.REGISTRY_SUPPLIERS)
                .build(),
            new NoopTargetBridge());
    AtomicBoolean initialized = new AtomicBoolean();

    runtime.initialize(
        "consumer",
        context -> {
          initialized.set(true);
          assertEquals("consumer", context.modId());
          context
              .commands()
              .register(CommandSpec.literal("portabletest", invocation -> CommandResult.SUCCESS));
        });

    assertTrue(initialized.get());
    assertEquals(1, runtime.commandSpecs().count());
    assertEquals(1, runtime.contexts().size());
  }

  private static final class NoopTargetBridge implements TargetBridge {
    @Override
    public Object toNativeIdentifier(Identifier id) {
      return id.toString();
    }

    @Override
    public <T> void register(RegistryKey<T> key, T value) {}
  }
}

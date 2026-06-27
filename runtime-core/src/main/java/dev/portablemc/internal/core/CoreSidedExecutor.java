package dev.portablemc.internal.core;

import dev.portablemc.api.runtime.PhysicalSide;
import dev.portablemc.api.runtime.SidedExecutor;
import java.util.Objects;

/** Default physical-side executor. */
public final class CoreSidedExecutor implements SidedExecutor {
  private final PhysicalSide physicalSide;

  public CoreSidedExecutor(PhysicalSide physicalSide) {
    this.physicalSide = Objects.requireNonNull(physicalSide, "physicalSide");
  }

  @Override
  public void runWhenClient(Runnable action) {
    Objects.requireNonNull(action, "action");
    if (physicalSide == PhysicalSide.CLIENT) {
      action.run();
    }
  }

  @Override
  public void runWhenDedicatedServer(Runnable action) {
    Objects.requireNonNull(action, "action");
    if (physicalSide == PhysicalSide.DEDICATED_SERVER) {
      action.run();
    }
  }

  @Override
  public PhysicalSide physicalSide() {
    return physicalSide;
  }
}

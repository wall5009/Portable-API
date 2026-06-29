/*
 * Copyright (c) 2026 PortableMC. All Rights Reserved.
 */
package dev.portablemc.api.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.portablemc.api.spi.PortableCommandAdapter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for portable command-tree validation and registration.
 */
final class PortableCommandTreeTest {
    @Test
    void registersNestedTreeThroughAdapter() {
        List<PortableCommandTree> trees = new ArrayList<>();
        PortableCommandManager manager = new PortableCommandManager(new TreeAdapter(trees));
        PortableCommandNode.Builder root = PortableCommandTree.literal("portable").requiresPermission(2);
        root.thenLiteral("config")
                .thenArgument("enabled", PortableCommandArgumentType.BOOLEAN)
                .executes(context -> 1);

        manager.registerTree(new PortableCommandTree(root.build()));

        assertEquals("portable", trees.get(0).root().name());
        assertEquals("config", trees.get(0).root().children().get(0).name());
    }

    @Test
    void rejectsDuplicateRoot() {
        PortableCommandManager manager = new PortableCommandManager(command -> { });

        manager.registerLiteral("portable", 0, context -> 1);

        assertThrows(IllegalStateException.class, () -> manager.registerTree(
                new PortableCommandTree(PortableCommandTree.literal("portable").executes(context -> 1).build())
        ));
    }

    private record TreeAdapter(List<PortableCommandTree> trees) implements PortableCommandAdapter {
        @Override
        public void register(final PortableCommand command) {
            throw new UnsupportedOperationException("Root literal fallback was not expected");
        }

        @Override
        public void registerTree(final PortableCommandTree tree) {
            trees.add(tree);
        }
    }
}

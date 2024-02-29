/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world;

import eu.darkcube.minigame.woolbattle.api.world.Block;

public interface BlockEvent {
    Block block();

    class Event implements BlockEvent {
        private final Block block;

        public Event(Block block) {
            this.block = block;
        }

        @Override public Block block() {
            return block;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements BlockEvent {
        private final Block block;

        public Cancellable(Block block) {
            this.block = block;
        }

        @Override public Block block() {
            return block;
        }
    }
}

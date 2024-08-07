/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.event.world.WorldEvent;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface EntityEvent extends WorldEvent {
    @NotNull Entity entity();

    @Override
    default @NotNull World world() {
        return entity().location().world();
    }

    class Event implements EntityEvent {
        private final @NotNull Entity entity;

        public Event(@NotNull Entity entity) {
            this.entity = entity;
        }

        @Override
        public @NotNull Entity entity() {
            return entity;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements EntityEvent {
        private final @NotNull Entity entity;

        public Cancellable(@NotNull Entity entity) {
            this.entity = entity;
        }

        @Override
        public @NotNull Entity entity() {
            return entity;
        }
    }
}

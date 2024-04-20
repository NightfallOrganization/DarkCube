/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.event.world.WorldEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface BlockEvent extends WorldEvent {
    @NotNull Block block();

    @Override
    default @NotNull World world() {
        return block().world();
    }

    class Event implements BlockEvent {
        private final @NotNull Block block;

        public Event(@NotNull Block block) {
            this.block = block;
        }

        @Override
        public @NotNull Block block() {
            return block;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements BlockEvent {
        private final @NotNull Block block;

        public Cancellable(@NotNull Block block) {
            this.block = block;
        }

        @Override
        public @NotNull Block block() {
            return block;
        }
    }

    class User extends UserEvent.Event implements BlockEvent {
        private final @NotNull Block block;

        public User(@NotNull WBUser user, @NotNull Block block) {
            super(user);
            this.block = block;
        }

        @Override
        public @NotNull Block block() {
            return block;
        }

        public static class Cancellable extends UserEvent.Cancellable implements BlockEvent {
            private final @NotNull Block block;

            public Cancellable(@NotNull WBUser user, @NotNull Block block) {
                super(user);
                this.block = block;
            }

            @Override
            public @NotNull Block block() {
                return block;
            }
        }
    }
}

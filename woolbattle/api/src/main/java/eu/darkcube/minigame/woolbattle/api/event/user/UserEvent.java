/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface UserEvent {
    @NotNull WBUser user();

    class Event implements UserEvent {
        private final @NotNull WBUser user;

        public Event(@NotNull WBUser user) {
            this.user = user;
        }

        @Override
        public @NotNull WBUser user() {
            return user;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements UserEvent {
        private final @NotNull WBUser user;

        public Cancellable(@NotNull WBUser user) {
            this.user = user;
        }

        @Override
        public @NotNull WBUser user() {
            return user;
        }
    }
}

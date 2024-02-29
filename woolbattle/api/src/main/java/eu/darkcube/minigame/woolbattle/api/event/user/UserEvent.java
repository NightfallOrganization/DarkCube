/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;

public interface UserEvent {
    WBUser user();

    class Event implements UserEvent {
        private final WBUser user;

        public Event(WBUser user) {
            this.user = user;
        }

        @Override public WBUser user() {
            return user;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements UserEvent {
        private final WBUser user;

        public Cancellable(WBUser user) {
            this.user = user;
        }

        @Override public WBUser user() {
            return user;
        }
    }
}

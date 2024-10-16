/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserChatEvent extends UserEvent.Event {
    private final String message;

    public UserChatEvent(@NotNull WBUser user, String message) {
        super(user);
        this.message = message;
    }

    public String message() {
        return message;
    }
}

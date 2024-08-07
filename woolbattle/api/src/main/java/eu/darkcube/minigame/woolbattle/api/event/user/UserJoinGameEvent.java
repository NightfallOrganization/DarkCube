/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.event.game.GameEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserJoinGameEvent extends GameEvent.User.Event {
    public UserJoinGameEvent(@NotNull WBUser user, @NotNull Game game) {
        super(user, game);
    }
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class IngameUserJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final CommonIngame ingame;

    public IngameUserJoinGameListener(CommonIngame ingame) {
        super(UserJoinGameEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        this.ingame.join((CommonWBUser) event.user());
    }
}

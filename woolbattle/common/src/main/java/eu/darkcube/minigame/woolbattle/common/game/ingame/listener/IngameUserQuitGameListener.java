/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.game.UserQuitGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListeners;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class IngameUserQuitGameListener implements ConfiguredListeners {
    private final CommonIngame ingame;
    private final EventNode<Object> node = EventNode.all("quit-game");

    public IngameUserQuitGameListener(CommonIngame ingame) {
        this.ingame = ingame;
        node.addListener(UserQuitGameEvent.class, this::handle);
    }

    private void handle(UserQuitGameEvent event) {
        ingame.quit((CommonWBUser) event.user());
    }

    @Override
    public @NotNull EventNode<?> node() {
        return node;
    }
}

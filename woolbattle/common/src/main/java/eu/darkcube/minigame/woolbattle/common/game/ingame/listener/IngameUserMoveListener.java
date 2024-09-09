/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.user.UserMoveEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class IngameUserMoveListener extends ConfiguredListener<UserMoveEvent> {
    private final CommonIngame ingame;

    public IngameUserMoveListener(CommonIngame ingame) {
        super(UserMoveEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(UserMoveEvent event) {
        var user = (CommonWBUser) event.user();
        var team = user.team();
        if (team != null) {
            var location = event.location();
            if (team.canPlay()) {
                if (location.blockY() < ingame.game().api().mapManager().deathHeight()) {
                    user.applyVoid();
                }
            }
            if (team.spectator()) {
                if (location.blockY() < ingame.game().api().mapManager().deathHeight() - 50) {
                    // Spectators can go 50 blocks lower
                    user.applyVoid();
                }
            }
        }

        ingame.heightDisplayScheduler().update(user);
    }
}

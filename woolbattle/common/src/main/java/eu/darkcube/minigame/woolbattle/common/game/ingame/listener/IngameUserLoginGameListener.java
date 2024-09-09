/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.listener;

import eu.darkcube.minigame.woolbattle.api.event.game.UserLoginGameEvent;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class IngameUserLoginGameListener extends ConfiguredListener<UserLoginGameEvent> {
    private final @NotNull CommonIngame ingame;

    public IngameUserLoginGameListener(@NotNull CommonIngame ingame) {
        super(UserLoginGameEvent.class);
        this.ingame = ingame;
    }

    @Override
    public void accept(UserLoginGameEvent event) {
        var user = (CommonWBUser) event.user();
        // User is not yet in spectator team
        var team = ingame.game().teamManager().spectator();
        event.result(UserLoginGameEvent.Result.USER_PLAYING);
        var spawn = ingame.mapIngameData().spawn(team.key());
        if (spawn == null) {
            ingame.game().api().woolbattle().logger().warn("No spawn configured for team {} on {}", team.key(), ingame.game().mapSize());
            spawn = new Position.Directed.Simple(0.5, 100, 0.5, 0, 0);
        }

        event.spawnLocation(new Location(ingame.world(), spawn));
        ingame.preJoin(user);
    }
}

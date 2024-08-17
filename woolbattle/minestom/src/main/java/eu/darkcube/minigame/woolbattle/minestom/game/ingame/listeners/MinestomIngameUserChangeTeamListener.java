/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.ingame.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserChangeTeamEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.ingame.MinestomIngame;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomIngameUserChangeTeamListener extends ConfiguredListener<UserChangeTeamEvent> {
    private final @NotNull MinestomWoolBattle woolbattle;
    private final @NotNull MinestomIngame ingame;

    public MinestomIngameUserChangeTeamListener(@NotNull MinestomWoolBattle woolbattle, @NotNull MinestomIngame ingame) {
        super(UserChangeTeamEvent.class);
        this.woolbattle = woolbattle;
        this.ingame = ingame;
    }

    @Override
    public void accept(UserChangeTeamEvent event) {
        var user = (CommonWBUser) event.user();
        var player = woolbattle.player(user);
        var oldTeam = (CommonTeam) event.oldTeam();
        var newTeam = (CommonTeam) event.newTeam();
        if (oldTeam != null && newTeam != null) {
            ingame.scoreboard().switchTeam(player, oldTeam, newTeam);
        } else if (oldTeam == null && newTeam == null) {
            // Can't happen
            throw new IllegalStateException("Player switched team from null to null");
        }
    }
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.lobby;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.game.lobby.LobbySidebarTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners.MinestomLobbyInventoryClickListener;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners.MinestomLobbyJoinGameListener;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners.MinestomLobbyUserChangeTeamListener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomLobby extends CommonLobby {
    private final @NotNull MinestomLobbyScoreboard scoreboard;
    private final @NotNull MinestomWoolBattle woolbattle;

    public MinestomLobby(@NotNull CommonGame game, @NotNull MinestomWoolBattle woolbattle) {
        super(game);
        this.woolbattle = woolbattle;
        this.listeners.addListener(new MinestomLobbyInventoryClickListener().create());
        this.listeners.addListener(new MinestomLobbyJoinGameListener(woolbattle).create());
        this.listeners.addListener(new MinestomLobbyUserChangeTeamListener(woolbattle, this).create());
        this.scoreboard = new MinestomLobbyScoreboard(woolbattle, game);
    }

    @Override
    protected void updateSidebar(@NotNull CommonWBUser user, LobbySidebarTeam team) {
        this.scoreboard.update(user, team);
    }

    public @NotNull MinestomLobbyScoreboard scoreboard() {
        return scoreboard;
    }
}

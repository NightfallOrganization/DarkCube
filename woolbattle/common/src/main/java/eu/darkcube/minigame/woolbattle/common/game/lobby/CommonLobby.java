/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonPhase;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyBreakBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyPlaceBlockListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserDropItemListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.listeners.LobbyUserJoinGameListener;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.GameState;

public class CommonLobby extends CommonPhase implements Lobby {
    protected CommonWorld world;
    protected Location spawn;

    public CommonLobby(@NotNull CommonGame game) {
        super(game, GameState.LOBBY);
        this.listeners.addListener(new LobbyBreakBlockListener().create());
        this.listeners.addListener(new LobbyPlaceBlockListener().create());
        this.listeners.addListener(new LobbyUserJoinGameListener(this).create());
        this.listeners.addListener(new LobbyUserDropItemListener().create());
    }

    @Override
    public void enable() {
        setupWorld();
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
        game.woolbattle().worldHandler().unloadWorld(world);
        world = null;
        spawn = null;
    }

    public Location spawn() {
        return spawn;
    }

    private void setupWorld() {
        world = game.woolbattle().worldHandler().loadLobbyWorld(game);
        var spawnPosition = game.lobbyData().spawn();
        spawn = new Location(world, spawnPosition);
    }
}
/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.GamePhaseCreator;
import eu.darkcube.minigame.woolbattle.common.game.endgame.CommonEndgame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.MinestomLobby;

public class MinestomGamePhaseCreator implements GamePhaseCreator {
    private final MinestomWoolBattle woolbattle;

    public MinestomGamePhaseCreator(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public CommonLobby createLobby(CommonGame game) {
        return new MinestomLobby(game, woolbattle);
    }

    @Override
    public CommonIngame createIngame(CommonGame game) {
        return new CommonIngame(game);
    }

    @Override
    public CommonEndgame createEndgame(CommonGame game) {
        return new CommonEndgame(game);
    }
}

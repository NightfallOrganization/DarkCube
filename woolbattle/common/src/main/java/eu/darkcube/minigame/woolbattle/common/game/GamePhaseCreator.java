/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game;

import eu.darkcube.minigame.woolbattle.common.game.endgame.CommonEndgame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;

public interface GamePhaseCreator {
    CommonLobby createLobby(CommonGame game);

    CommonIngame createIngame(CommonGame game);

    CommonEndgame createEndgame(CommonGame game);
}

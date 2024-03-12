/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import eu.darkcube.minigame.woolbattle.api.game.lobby.LobbyData;
import eu.darkcube.minigame.woolbattle.api.util.WoolBattlePersistentDataTypes;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;

public class CommonLobbyData implements LobbyData {
    private Position.Directed spawn;

    @Override
    public @NotNull Position.Directed spawn() {
        return spawn;
    }

    @Override
    public void spawn(@NotNull Position.Directed spawn) {
        this.spawn = spawn;
    }

    public void load(CommonGame game) {
        spawn = game.woolbattle().persistentDataStorage().get(lobbySpawn(game), WoolBattlePersistentDataTypes.POSITION_DIRECTED, () -> new Position.Directed.Simple(0.5, 100, 0.5, 0, 0));
    }

    private Key lobbySpawn(CommonGame game) {
        return new Key(game.woolbattle(), "lobbySpawn");
    }
}

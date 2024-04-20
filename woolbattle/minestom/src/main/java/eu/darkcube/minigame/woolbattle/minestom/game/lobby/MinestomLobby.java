/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.lobby;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners.MinestomLobbyInventoryClickListener;
import eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners.MinestomLobbySetupUserListener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomLobby extends CommonLobby {
    public MinestomLobby(@NotNull CommonGame game, MinestomWoolBattle woolbattle) {
        super(game);
        this.listeners.addListener(new MinestomLobbyInventoryClickListener().create());
        this.listeners.addListener(new MinestomLobbySetupUserListener(woolbattle).create());
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
    }
}

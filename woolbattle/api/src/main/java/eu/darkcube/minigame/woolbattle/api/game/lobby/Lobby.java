/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.game.lobby;

import eu.darkcube.minigame.woolbattle.api.game.GamePhase;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface Lobby extends GamePhase {
    @NotNull
    Location spawn();

    @Nullable
    World world();

    void forceLifes(int lifes);

    int lifes();
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.game.lobby;

import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface LobbyData {
    @Api
    @NotNull
    Position.Directed spawn();

    @Api
    void spawn(@NotNull Position.Directed spawn);
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.game;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

@Api
public interface GameManager {
    @Api
    @UnmodifiableView
    @NotNull
    Collection<? extends Game> games();

    @Api
    @NotNull
    Game createGame();

    @Api
    @Nullable
    Game game(@NotNull UUID id);
}

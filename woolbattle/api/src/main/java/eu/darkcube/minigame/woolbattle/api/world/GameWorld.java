/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import java.util.Locale;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface GameWorld extends World {
    @Api
    @NotNull
    Game game();

    @SuppressWarnings("PatternValidation")
    @Override
    default @NotNull Key key() {
        var string = game().id().toString().toLowerCase(Locale.ROOT);
        return Key.key("game", string);
    }
}

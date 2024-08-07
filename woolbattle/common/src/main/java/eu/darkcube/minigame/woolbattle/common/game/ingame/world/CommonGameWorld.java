/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.world;

import eu.darkcube.minigame.woolbattle.api.world.GameWorld;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonGameWorld extends CommonWorld implements GameWorld {
    protected final @NotNull CommonGame game;

    public CommonGameWorld(@NotNull CommonGame game) {
        super(game.woolbattle().worldHandler());
        this.game = game;
    }

    @Override
    public @NotNull CommonGame game() {
        return game;
    }
}

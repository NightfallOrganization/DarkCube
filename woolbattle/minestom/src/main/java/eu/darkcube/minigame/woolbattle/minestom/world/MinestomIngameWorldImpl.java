/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import net.minestom.server.instance.Instance;

public class MinestomIngameWorldImpl extends CommonWorld implements MinestomWorld {
    private final Instance instance;

    public MinestomIngameWorldImpl(CommonGame game, Instance instance) {
        super(game);
        this.instance = instance;
    }

    @Override
    public Instance instance() {
        return instance;
    }
}

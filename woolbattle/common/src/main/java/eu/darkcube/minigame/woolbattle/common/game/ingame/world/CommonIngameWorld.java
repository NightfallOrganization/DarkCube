/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.world;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.world.CommonBlock;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;

public class CommonIngameWorld extends CommonWorld {
    private final Set<CommonBlock> placedBlocks = ConcurrentHashMap.newKeySet();
    private final Map<CommonBlock, ColoredWool> brokenWool = new ConcurrentHashMap<>();
    private final Key blockDamageKey;

    public CommonIngameWorld(CommonGame game) {
        super(game);
        this.blockDamageKey = new Key(game.woolbattle(), "blockDamage");
    }

    public Set<CommonBlock> placedBlocks() {
        return placedBlocks;
    }

    public Key blockDamageKey() {
        return blockDamageKey;
    }

    public Map<CommonBlock, ColoredWool> brokenWool() {
        return brokenWool;
    }

    @Override
    public @NotNull CommonIngameBlock blockAt(int x, int y, int z) {
        return new CommonIngameBlock(this, x, y, z, game.ingameData().maxBlockDamage());
    }
}

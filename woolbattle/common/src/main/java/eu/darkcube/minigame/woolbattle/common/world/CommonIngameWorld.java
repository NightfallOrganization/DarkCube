/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameBlock;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public abstract class CommonIngameWorld extends CommonGameWorld {
    private final Set<CommonBlock> placedBlocks = ConcurrentHashMap.newKeySet();
    private final Map<CommonBlock, ColoredWool> brokenWool = new ConcurrentHashMap<>();
    private final Key blockDamageKey;

    public CommonIngameWorld(@NotNull CommonGame game) {
        super(game);
        this.blockDamageKey = Key.key(game.woolbattle(), "block_damage");
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

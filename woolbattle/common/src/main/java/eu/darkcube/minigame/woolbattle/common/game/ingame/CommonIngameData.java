/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame;

import eu.darkcube.minigame.woolbattle.api.game.ingame.IngameData;

public class CommonIngameData implements IngameData {
    private int maxBlockDamage = 3;

    @Override
    public int maxBlockDamage() {
        return maxBlockDamage;
    }

    @Override
    public void maxBlockDamage(int maxBlockDamage) {
        this.maxBlockDamage = maxBlockDamage;
    }
}

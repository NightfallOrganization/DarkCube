/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;

public interface CommonColoredWool extends ColoredWool {
    void unsafeApply(Block block);
}

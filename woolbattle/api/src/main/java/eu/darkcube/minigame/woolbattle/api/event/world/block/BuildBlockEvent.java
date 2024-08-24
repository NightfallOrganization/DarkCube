/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A block is built as result of some action. No clear origin, may be by perk, user, or otherwise
 */
public class BuildBlockEvent extends BlockEvent.Cancellable {
    public BuildBlockEvent(@NotNull Block block) {
        super(block);
    }
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.world.Block;

/**
 * A block is destroyed as a result of damage actions. No clear origin.
 */
public class DestroyBlockEvent extends BlockEvent.Cancellable {
    public DestroyBlockEvent(Block block) {
        super(block);
    }
}

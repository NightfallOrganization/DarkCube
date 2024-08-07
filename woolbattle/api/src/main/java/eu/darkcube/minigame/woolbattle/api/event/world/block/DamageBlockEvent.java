/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.world.Block;

public class DamageBlockEvent extends BlockEvent.Cancellable {
    private final int oldDamage;
    private int newDamage;

    public DamageBlockEvent(Block block, int oldDamage, int newDamage) {
        super(block);
        this.oldDamage = oldDamage;
        this.newDamage = newDamage;
    }

    public int oldDamage() {
        return oldDamage;
    }

    public int newDamage() {
        return newDamage;
    }

    public void newDamage(int newDamage) {
        this.newDamage = newDamage;
    }
}

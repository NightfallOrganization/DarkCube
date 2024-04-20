/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.ingame.world;

import eu.darkcube.minigame.woolbattle.api.event.world.block.DamageBlockEvent;
import eu.darkcube.minigame.woolbattle.api.event.world.block.DestroyBlockEvent;
import eu.darkcube.minigame.woolbattle.common.world.CommonBlock;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.system.server.item.material.Material;

public class CommonIngameBlock extends CommonBlock {
    private final CommonIngameWorld world;
    protected final int maxBlockDamage;

    public CommonIngameBlock(CommonIngameWorld world, int x, int y, int z, int maxBlockDamage) {
        super(world, x, y, z);
        this.world = world;
        this.maxBlockDamage = maxBlockDamage;
    }

    @Override
    public int blockDamage() {
        return metadata().getOr(world.blockDamageKey(), 0);
    }

    private void blockDamage(int blockDamage) {
        metadata().set(world.blockDamageKey(), blockDamage);
    }

    @Override
    public void incrementBlockDamage(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Block Damage <= 0");
        var currentDamage = blockDamage();
        var newDamage = currentDamage + amount;
        var event = new DamageBlockEvent(this, currentDamage, newDamage);
        world.game().eventManager().call(event);
        if (event.cancelled()) {
            return;
        }
        if (newDamage >= maxBlockDamage) {
            if (destroy()) {
                return;
            }
        }
        blockDamage(newDamage);
    }

    private boolean destroy() {
        return destroy(false);
    }

    private boolean destroy(boolean force) {
        if (!world.placedBlocks().contains(this)) {
            if (!force) {
                return false;
            }
        }
        var event = new DestroyBlockEvent(this);
        world.game().eventManager().call(event);
        if (event.cancelled() && !force) return false;
        var materialProvider = world.game().woolbattle().materialProvider();
        if (!world.placedBlocks().remove(this) && materialProvider.isWool(material())) {
            world.brokenWool().put(this, materialProvider.woolFrom(this));
        }

        metadata().clear();
        material(Material.air());
        return true;
    }
}

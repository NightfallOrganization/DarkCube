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
import eu.darkcube.minigame.woolbattle.common.world.CommonColoredWool;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.system.server.item.material.Material;

public class CommonIngameBlock extends CommonBlock {
    protected final CommonIngameWorld world;
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

    public int maxBlockDamage() {
        return maxBlockDamage;
    }

    @Override
    public void incrementBlockDamage(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Block Damage <= 0");
        var currentDamage = blockDamage();
        var newDamage = currentDamage + amount;
        changeBlockDamage(currentDamage, newDamage);
    }

    public void changeBlockDamage(int newDamage) {
        if (newDamage <= 0) throw new IllegalArgumentException("Block Damage <= 0");
        changeBlockDamage(blockDamage(), newDamage);
    }

    private void changeBlockDamage(int currentDamage, int newDamage) {
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

    @Override
    public boolean isWoolGenerator() {
        var playerPlaced = world.placedBlocks().contains(this);
        if (playerPlaced) return false;
        if (world.brokenWool().containsKey(this)) {
            return true;
        }
        return world.game.api().materialProvider().isWool(material());
    }

    public void regenerateTo(CommonColoredWool wool) {
        wool.unsafeApply(this);
        // A user could have placed a block into the generator
        world.placedBlocks().remove(this);
        var metadata = metadata();
        // Reset metadata to clear any perk-specific data, or similar.
        // Blank wool blocks count as generators, which is what we need
        metadata.clear();
    }

    private boolean destroy() {
        return destroy(false);
    }

    private boolean destroy(boolean force) {
        var materialProvider = world.game().api().materialProvider();
        var material = material();
        var isWool = materialProvider.isWool(material);
        if (!world.placedBlocks().contains(this) && !isWool) {
            if (!force) {
                return false;
            }
        }
        var event = new DestroyBlockEvent(this);
        world.game().eventManager().call(event);
        if (event.cancelled() && !force) return false;
        if (!world.placedBlocks().remove(this) && isWool) {
            world.brokenWool().put(this, materialProvider.woolFrom(this));
        }

        metadata().clear();
        material(Material.air());
        return true;
    }
}

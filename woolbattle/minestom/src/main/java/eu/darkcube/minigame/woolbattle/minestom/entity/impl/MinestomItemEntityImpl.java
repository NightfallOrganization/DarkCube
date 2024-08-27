/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import java.time.Duration;

import eu.darkcube.minigame.woolbattle.api.entity.ItemEntity;
import eu.darkcube.minigame.woolbattle.api.world.GameWorld;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;

public class MinestomItemEntityImpl extends net.minestom.server.entity.ItemEntity implements EntityImpl {
    private final MinestomWoolBattle woolbattle;
    private long mergeDelay = Duration.of(10, TimeUnit.SERVER_TICK).toMillis();
    private ItemEntity handle;

    static {
        setMergeDelay(Duration.ZERO);
    }

    public MinestomItemEntityImpl(@NotNull ItemStack itemStack, MinestomWoolBattle woolbattle) {
        super(itemStack);
        this.woolbattle = woolbattle;
    }

    @Override
    public void update(long time) {
        super.update(time);
        if (!instance.getBlock(position).isAir()) {
            remove();
            return;
        }
        if (instance == null) return;
        var world = woolbattle.worlds().get(instance);
        if (!(world instanceof GameWorld)) {
            remove();
            return;
        }
        var deathHeight = woolbattle.api().mapManager().deathHeight();
        if (position.y() < deathHeight) {
            remove();
        }
    }

    @Override
    public boolean isMergeable() {
        return super.isMergeable() && (System.currentTimeMillis() - getSpawnTime() >= mergeDelay);
    }

    public void setMergeDelay0(@NotNull Duration mergeDelay) {
        this.mergeDelay = mergeDelay.toMillis();
    }

    public long getMergeDelay0() {
        return this.mergeDelay;
    }

    @Override
    @NotNull
    public ItemEntity handle() {
        return handle;
    }

    @Override
    public void handle(eu.darkcube.minigame.woolbattle.api.entity.Entity handle) {
        this.handle = (ItemEntity) handle;
    }
}

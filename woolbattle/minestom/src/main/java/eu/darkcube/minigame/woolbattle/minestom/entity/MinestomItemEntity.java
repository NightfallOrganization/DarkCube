/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import java.time.Duration;

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.ItemEntity;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.MinestomItemEntityImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.CollectItemPacket;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.utils.time.Tick;

@SuppressWarnings("UnstableApiUsage")
public class MinestomItemEntity extends MinestomEntity implements ItemEntity {
    public MinestomItemEntity(Acquirable<? extends Entity> entity, MinestomWoolBattle woolbattle) {
        super(entity, EntityType.ITEM, woolbattle);
    }

    @Override
    public @NotNull ItemBuilder item() {
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        var itemStack = entity.getItemStack();
        lock.unlock();
        return ItemBuilder.item(itemStack);
    }

    @Override
    public void item(@NotNull ItemBuilder item) {
        var itemStack = item.<ItemStack>build();
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        entity.setItemStack(itemStack);
        lock.unlock();
    }

    @Override
    public int pickupDelay() {
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        var millis = entity.getPickupDelay();
        lock.unlock();
        return Tick.SERVER_TICKS.fromDuration(Duration.ofMillis(millis));
    }

    @Override
    public void pickupDelay(int delay) {
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        entity.setPickupDelay(delay, Tick.SERVER_TICKS);
        lock.unlock();
    }

    @Override
    public int mergeDelay() {
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        var millis = entity.getMergeDelay0();
        lock.unlock();
        return Tick.SERVER_TICKS.fromDuration(Duration.of(millis, Tick.SERVER_TICKS));
    }

    @Override
    public void mergeDelay(int delay) {
        var lock = entity.lock();
        var entity = (MinestomItemEntityImpl) lock.get();
        entity.setMergeDelay0(Duration.of(delay, Tick.SERVER_TICKS));
        lock.unlock();
    }

    @Override
    public void sendPickupPacket(@NotNull WBUser collector, int amount) {
        var player = woolbattle.player((CommonWBUser) collector);
        player.sendPacketToViewersAndSelf(new CollectItemPacket(entity.unwrap().getEntityId(), player.getEntityId(), amount));
    }
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.world;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class EventDamageBlock extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final int oldDamage;
    private int newDamage;
    private boolean cancel = false;

    public EventDamageBlock(Block theBlock, int oldDamage, int newDamage) {
        super(theBlock);
        this.oldDamage = oldDamage;
        this.newDamage = newDamage;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int getNewDamage() {
        return newDamage;
    }

    public void setNewDamage(int newDamage) {
        this.newDamage = newDamage;
    }

    public int getOldDamage() {
        return oldDamage;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }

    @Override public boolean isCancelled() {
        return cancel;
    }

    @Override public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}

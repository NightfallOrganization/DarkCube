/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when the woolcount of a player decreases
 */
public class EventUserRemoveWool extends UserEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private int amount;

    public EventUserRemoveWool(WBUser user, int amount) {
        super(user);
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public int amount() {
        return amount;
    }

    public void amount(int amount) {
        this.amount = amount;
    }

    @Override public boolean isCancelled() {
        return cancel;
    }

    @Override public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}

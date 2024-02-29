/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class EventUserKill extends UserEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private WBUser killer;
    private boolean cancel;

    public EventUserKill(WBUser user, WBUser killer) {
        super(user);
        this.killer = killer;
        cancel = false;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WBUser killer() {
        return killer;
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

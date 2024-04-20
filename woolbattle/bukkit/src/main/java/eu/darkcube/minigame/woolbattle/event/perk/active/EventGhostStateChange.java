/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.perk.active;

import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.HandlerList;

public class EventGhostStateChange extends UserEvent {
    private static final HandlerList handlers = new HandlerList();
    private final boolean ghost;

    public EventGhostStateChange(WBUser user, boolean ghost) {
        super(user);
        this.ghost = ghost;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean ghost() {
        return ghost;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

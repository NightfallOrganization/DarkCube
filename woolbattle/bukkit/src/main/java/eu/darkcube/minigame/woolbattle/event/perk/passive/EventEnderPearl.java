/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.perk.passive;

import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.HandlerList;

public class EventEnderPearl extends UserEvent {
    private static final HandlerList handlers = new HandlerList();
    private final boolean canElevate;
    private boolean elevate;

    public EventEnderPearl(WBUser user, boolean canElevate, boolean elevate) {
        super(user);
        this.canElevate = canElevate;
        this.elevate = elevate;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean canElevate() {
        return canElevate;
    }

    public boolean elevate() {
        return elevate;
    }

    public void elevate(boolean elevate) {
        this.elevate = elevate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.HandlerList;

public class EventUserAttackUser extends UserEvent {
    private static final HandlerList handlers = new HandlerList();
    private final WBUser target;

    public EventUserAttackUser(WBUser user, WBUser target) {
        super(user);
        this.target = target;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WBUser target() {
        return target;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}

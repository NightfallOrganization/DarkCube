/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.perk.other;

import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.entity.Arrow;
import org.bukkit.event.HandlerList;

public class BowShootArrowEvent extends UserEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Arrow arrow;

    public BowShootArrowEvent(WBUser user, Arrow arrow) {
        super(user);
        this.arrow = arrow;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Arrow arrow() {
        return arrow;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

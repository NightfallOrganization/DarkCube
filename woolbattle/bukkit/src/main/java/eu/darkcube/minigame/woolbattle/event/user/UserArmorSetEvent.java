/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Color;
import org.bukkit.event.HandlerList;

public class UserArmorSetEvent extends UserEvent {
    private static final HandlerList handlers = new HandlerList();

    private Color color;

    public UserArmorSetEvent(WBUser user, Color color) {
        super(user);
        this.color = color;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }

    @Override public HandlerList getHandlers() {
        return handlers;
    }
}

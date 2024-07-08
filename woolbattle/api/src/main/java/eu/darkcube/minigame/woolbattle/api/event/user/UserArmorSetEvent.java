/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.util.Color;

public class UserArmorSetEvent extends UserEvent.Event {
    private Color color;

    public UserArmorSetEvent(WBUser user, Color color) {
        super(user);
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public void color(Color color) {
        this.color = color;
    }
}
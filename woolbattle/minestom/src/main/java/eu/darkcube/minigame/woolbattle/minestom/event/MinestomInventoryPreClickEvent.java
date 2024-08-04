/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.event;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class MinestomInventoryPreClickEvent extends UserEvent.Event {
    private final @NotNull InventoryPreClickEvent event;

    public MinestomInventoryPreClickEvent(@NotNull WBUser user, @NotNull InventoryPreClickEvent event) {
        super(user);
        this.event = event;
    }

    public @NotNull InventoryPreClickEvent event() {
        return event;
    }
}

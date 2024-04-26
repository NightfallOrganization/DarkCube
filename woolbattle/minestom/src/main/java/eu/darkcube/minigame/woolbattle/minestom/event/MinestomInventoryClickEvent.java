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
import net.minestom.server.event.inventory.InventoryClickEvent;

public class MinestomInventoryClickEvent extends UserEvent.Event {
    private final @NotNull InventoryClickEvent event;

    public MinestomInventoryClickEvent(@NotNull WBUser user, @NotNull InventoryClickEvent event) {
        super(user);
        this.event = event;
    }

    public @NotNull InventoryClickEvent event() {
        return event;
    }
}

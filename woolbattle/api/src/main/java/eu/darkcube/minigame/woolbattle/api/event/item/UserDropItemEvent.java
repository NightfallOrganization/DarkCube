/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.item;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class UserDropItemEvent extends UserEvent.Cancellable implements ItemEvent {
    private final @NotNull ItemBuilder item;

    public UserDropItemEvent(@NotNull WBUser user, @NotNull ItemBuilder item) {
        super(user);
        this.item = item;
    }

    @Override
    public @NotNull ItemBuilder item() {
        return item;
    }
}

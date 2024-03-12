/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;

public class UserInteractEvent extends UserEvent.Cancellable {
    private final @Nullable ItemBuilder item;
    private final @NotNull Action action;

    public UserInteractEvent(@NotNull WBUser user, @Nullable ItemBuilder item, @NotNull Action action) {
        super(user);
        this.item = item;
        this.action = action;
    }

    public @Nullable ItemBuilder item() {
        return item;
    }

    public @NotNull Action action() {
        return action;
    }

    public enum Action {
        LEFT_CLICK_BLOCK,
        LEFT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        RIGHT_CLICK_AIR
    }
}

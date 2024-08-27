/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.item;

import eu.darkcube.minigame.woolbattle.api.entity.ItemEntity;
import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class UserPickupItemEvent extends UserEvent.Cancellable implements ItemEvent {
    private final @NotNull ItemEntity entity;
    private @NotNull ItemBuilder item;

    public UserPickupItemEvent(@NotNull WBUser user, @NotNull ItemEntity entity, @NotNull ItemBuilder item) {
        super(user);
        this.entity = entity;
        this.item = item;
    }

    @Override
    public @NotNull ItemBuilder item() {
        return item;
    }

    public void item(@NotNull ItemBuilder item) {
        this.item = item;
    }

    public @NotNull ItemEntity entity() {
        return entity;
    }
}

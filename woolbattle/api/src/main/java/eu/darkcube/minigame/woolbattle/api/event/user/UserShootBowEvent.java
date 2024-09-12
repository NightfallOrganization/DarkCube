/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.event.item.ItemEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class UserShootBowEvent extends UserEvent.Event implements ItemEvent {
    private final @NotNull ItemBuilder item;
    private final float power;

    public UserShootBowEvent(@NotNull WBUser user, @NotNull ItemBuilder item, float power) {
        super(user);
        this.item = item;
        this.power = power;
    }

    @Override
    public @NotNull ItemBuilder item() {
        return item;
    }

    public float power() {
        return power;
    }
}

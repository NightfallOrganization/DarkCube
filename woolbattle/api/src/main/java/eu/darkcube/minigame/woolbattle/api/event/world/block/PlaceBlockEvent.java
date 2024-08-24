/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public class PlaceBlockEvent extends BlockEvent.User.Cancellable {
    private final @NotNull ItemBuilder item;

    public PlaceBlockEvent(@NotNull WBUser user, @NotNull Block block, @NotNull ItemBuilder item) {
        super(user, block);
        this.item = item;
    }

    public @NotNull ItemBuilder item() {
        return item;
    }
}

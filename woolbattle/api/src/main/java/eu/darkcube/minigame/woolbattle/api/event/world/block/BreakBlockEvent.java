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

public class BreakBlockEvent extends BlockEvent.User.Cancellable {
    public BreakBlockEvent(@NotNull WBUser user, @NotNull Block block) {
        super(user, block);
    }
}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface WoolBattleCommands {
    @Api
    void register(@NotNull WoolBattleCommand command);

    @Api
    void unregister(@NotNull WoolBattleCommand command);
}

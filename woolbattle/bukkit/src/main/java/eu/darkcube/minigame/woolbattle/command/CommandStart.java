/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;

public class CommandStart extends WBCommandExecutor {

    public CommandStart(WoolBattleBukkit woolbattle) {
        super("start", b -> b.executes(ctx -> {
            woolbattle.lobby().setOverrideTimer(0);
            return 0;
        }));
    }
}

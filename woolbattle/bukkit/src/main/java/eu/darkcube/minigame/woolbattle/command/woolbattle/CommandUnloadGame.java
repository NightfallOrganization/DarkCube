/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandUnloadGame extends WBCommandExecutor {
    public CommandUnloadGame(WoolBattleBukkit woolbattle) {
        super("unloadGame", b -> b.requires(ignoredCtx -> woolbattle.lobby().enabled()).executes(ctx -> {
            woolbattle.lobby().unloadGame();
            ctx.getSource().sendMessage(Component.text("Spiel entladen"));
            return 0;
        }));
    }
}

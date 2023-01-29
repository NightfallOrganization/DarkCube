/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.command;

import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.miners.player.TNTManager;

public class CommandTest extends CommandExecutor {

    public CommandTest() {
        super("miners", "test", new String[0], b -> b.executes(context -> test(context.getSource().asPlayer())));
    }

    public static int test(Player p) {
        TNTManager.explodeTNT(p.getLocation(), p, 10);
        return 0;
    }

}

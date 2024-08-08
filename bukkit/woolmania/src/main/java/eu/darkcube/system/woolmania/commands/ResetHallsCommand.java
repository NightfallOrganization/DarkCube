/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import static eu.darkcube.system.woolmania.enums.Names.SYSTEM;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.enums.Names;
import eu.darkcube.system.woolmania.util.message.Message;
import eu.darkcube.system.woolmania.util.RegenerateWoolAreas;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetHallsCommand extends WoolManiaCommand {

    public ResetHallsCommand() {
        super("resethalls", new String[0], builder -> builder.executes(context -> {

            RegenerateWoolAreas.regenerateWoolAreas();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                onlinePlayer.sendMessage(" \n" + "§7--------- " + SYSTEM.getName() + " ---------\n" + " \n" + Message.HALLS_RESET, context.getSource().asPlayer().getName() + " \n" + "§7---------------------------");
            }

            return 0;
        }));
    }

}

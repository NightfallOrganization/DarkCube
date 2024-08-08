/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import static eu.darkcube.system.woolmania.enums.Names.SYSTEM;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.util.message.Message;
import eu.darkcube.system.woolmania.util.area.RegenerateWoolAreas;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetHallsCommand extends WoolManiaCommand {

    public ResetHallsCommand() {
        super("resethalls", new String[0], builder -> builder.executes(context -> {

            RegenerateWoolAreas.regenerateWoolAreas();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                User user = UserAPI.instance().user(onlinePlayer.getUniqueId());

                BaseMessage message = Message.HALLS_RESET;
                message = message.prepend("\n§7--------- " + SYSTEM.getName() + " §7---------\n\n");
                message = message.append("\n\n§7-]-------------------------[-\n");

                user.sendMessage(message, context.getSource().getName());

            }

            return 0;
        }));
    }

}

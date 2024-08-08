/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.zenum;

import static eu.darkcube.system.woolmania.enums.Names.SYSTEM;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import eu.darkcube.system.woolmania.util.RegenerateWoolAreas;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SetZenumCommand extends WoolManiaCommand {

    public SetZenumCommand() {
        super("set", builder -> builder.executes(context -> {

            Player player = context.getSource().asPlayer();
            int money = WoolMania.getStaticPlayer(player).getMoney();

            return 0;
        }));
    }

}

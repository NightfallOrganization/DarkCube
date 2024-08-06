/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import static eu.darkcube.system.woolmania.enums.Names.SYSTEM;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.woolmania.util.RegenerateWoolAreas;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RegenerateHallsCommand extends WoolManiaCommand {

    public RegenerateHallsCommand() {
        super("regeneratehalls", new String[0], builder -> builder.executes(context -> {

            RegenerateWoolAreas.regenerateWoolAreas();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(SYSTEM.getName() + "Die Hallen wurden resettet");
            }

            return 0;
        }));
    }

}

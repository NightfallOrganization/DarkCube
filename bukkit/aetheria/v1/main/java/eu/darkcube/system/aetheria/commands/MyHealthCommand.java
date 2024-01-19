/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyHealthCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public MyHealthCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            int health = healthManager.getHealth(player);
            int maxHealth = healthManager.getMaxHealth(player);
            player.sendMessage("§7Du hast §a" + health + " §7von §a" + maxHealth + " §7Health");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}

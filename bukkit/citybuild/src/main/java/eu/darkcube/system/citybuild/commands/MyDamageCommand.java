/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.DamageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MyDamageCommand implements CommandExecutor {
    private DamageManager damageManager;

    public MyDamageCommand(DamageManager damageManager) {
        this.damageManager = damageManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            double damage = this.damageManager.getDamage(player);
            player.sendMessage("§7Du hast §a" + damage + " §7Damage");
            return true;
        } else {
            sender.sendMessage("§7Dieser Befehl kann nur von einem §aSpieler §7ausgeführt werden");
            return false;
        }
    }
}

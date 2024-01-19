/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.DamageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public class SetDamageCommand implements CommandExecutor {

    private final DamageManager damageManager;

    public SetDamageCommand(DamageManager damageManager) {
        this.damageManager = damageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setdamage [player] [value]");
            return true;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        try {
            double damage = Double.parseDouble(args[1]);
            damageManager.setDamage(player, damage);
            sender.sendMessage("§7Der Damage wurde für §a" + player.getName() + " §7zu §a" + damage + " §7gesetzt");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid damage value.");
        }

        return true;
    }

}

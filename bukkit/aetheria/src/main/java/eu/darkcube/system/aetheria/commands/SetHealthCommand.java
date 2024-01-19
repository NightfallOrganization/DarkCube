/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.HealthManager;
import eu.darkcube.system.aetheria.manager.player.MaxHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetHealthCommand implements CommandExecutor {
    private final HealthManager healthManager;
    private final MaxHealthManager maxHealthManager;

    public SetHealthCommand(HealthManager healthManager, MaxHealthManager maxHealthManager) {
        this.healthManager = healthManager;
        this.maxHealthManager = maxHealthManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /sethealth [player] [value]");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        int health;
        try {
            health = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid health value.");
            return true;
        }

        healthManager.setHealth(targetPlayer, health);
        maxHealthManager.setMaxHealth(targetPlayer, health);
        sender.sendMessage("§7Die Health von §a" + targetPlayer.getName() + " §7wurden zu §a" + health + " §7gesetzt");
        return true;
    }
}

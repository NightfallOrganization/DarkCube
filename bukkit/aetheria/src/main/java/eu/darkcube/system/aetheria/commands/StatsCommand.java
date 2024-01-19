/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private PlayerManager playerManager;

    public StatsCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("stats")) {
            double health = playerManager.getHealthManager().getHealth(player);
            int coreValue = playerManager.getCoreManager().getCoreValue(player);
            double damage = playerManager.getDamageManager().getDamage(player);
            double xp = playerManager.getXPManager().getXP(player);
            int level = playerManager.getLevelManager().getLevel(player);
            double maxhealth = playerManager.getMaxHealthManager().getMaxHealth(player);
            double regeneration = playerManager.getRegenerationManager().getRegenerationRate(player);

            player.sendMessage(" ");
            player.sendMessage("§7--- §aSpielerstatistiken §7---");
            player.sendMessage(" ");
            player.sendMessage("§7Gesundheit: §a" + health + " §7/ §a" + maxhealth);
            player.sendMessage("§7Regeneration: §a" + regeneration);
            player.sendMessage("§7Core: §a" + coreValue);
            player.sendMessage("§7Schaden: §a" + damage);
            player.sendMessage("§7Erfahrung: §a" + xp);
            player.sendMessage("§7Level: §a" + level);
        }

        return true;
    }
}

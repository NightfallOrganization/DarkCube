/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomHealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public HealCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        if (!command.getName().equalsIgnoreCase("heal") || (args.length > 1)) {
            sender.sendMessage("§7Unbekannter Befehl. Nutze §a/heal (Person) §7um dich oder andere zu heilen");
            return false;
        }

        if ((args.length == 1) && (Bukkit.getPlayer(args[0]) != null)) {
            Player player = Bukkit.getPlayer(args[0]);

            int maxHealth = healthManager.getMaxHealth(player);
            healthManager.setHealth(player, maxHealth);
            sender.sendMessage("§a" + player.getName() + "§7 wurde geheilt");
            return true;
        } else if (args.length == 0) {
            Player player = (Player) sender;

            int maxHealth = healthManager.getMaxHealth(player);
            healthManager.setHealth(player, maxHealth);
            sender.sendMessage("§7Du wurdest §ageheilt");
            return true;
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/heal (Person) §7um dich oder andere zu heilen");
        return false;
    }
}

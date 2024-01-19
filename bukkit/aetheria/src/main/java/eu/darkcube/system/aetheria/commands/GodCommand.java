/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            toggleGodMode((Player) sender, sender);
            return true;
        } else if (args.length == 1) {
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target != null) {
                toggleGodMode(target, sender);
                sender.sendMessage("§7God mode toggled for §a" + target.getName());
                return true;
            } else {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return false;
            }
        } else {
            sender.sendMessage("§cUsage: /god [player]");
            return false;
        }
    }

    private void toggleGodMode(Player player, CommandSender sender) {
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            player.sendMessage("§7Godmodus §aAUS");
            if (sender != player) {
                sender.sendMessage("§7God mode disabled for §a" + player.getName());
            }
        } else {
            player.setInvulnerable(true);
            player.sendMessage("§7Godmodus §aAN");
            if (sender != player) {
                sender.sendMessage("§7God mode enabled for §a" + player.getName());
            }
        }
    }

}

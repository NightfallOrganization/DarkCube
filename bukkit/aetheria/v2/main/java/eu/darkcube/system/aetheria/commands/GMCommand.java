/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GMCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0 || args.length > 2) {
            player.sendMessage("§cUsage: /gm [number] [player]");
            return false; // Falsche Anzahl an Argumenten
        }

        GameMode gameMode;
        switch (args[0]) {
            case "0":
                gameMode = GameMode.SURVIVAL;
                break;
            case "1":
                gameMode = GameMode.CREATIVE;
                break;
            case "2":
                gameMode = GameMode.ADVENTURE;
                break;
            case "3":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                player.sendMessage("§cUsage: /gm [value] [player]");
                return false; // Ungültiger Spielmodus
        }

        if (args.length == 1) {
            player.setGameMode(gameMode);
            player.sendMessage("§7Dein Spielmodus wurde zu §a" + gameMode.name() + " §7geändert");
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }
            target.setGameMode(gameMode);
            target.sendMessage("§7Dein Spielmodus wurde zu §a" + gameMode.name() + " §7geändert");
            player.sendMessage("§7Der Gamemode von §a" + target.getName() + " §7wurde auf §a " + gameMode.name() + " §7gesetzt");
        }

        return true;
    }
}

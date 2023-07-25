/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GM implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden");
			return false;
		}


		if(!command.getName().equalsIgnoreCase("gm") || !(args.length == 1 || args.length == 2)) {
			sender.sendMessage("§7Unbekannter Befehl. Nutze §b/gm 1 §7um den Gamemode auf Creative zu setzen");
			return false;
		}


		if (args.length == 2 && Bukkit.getPlayer(args[1])!=null) {
			Player player = Bukkit.getPlayer(args[1]);

			if (args[0].equalsIgnoreCase("1")) {

				player.setGameMode(GameMode.CREATIVE);
				sender.sendMessage("§7Der Gamemode von "+ player.getName() +" wurde auf §bCreative §7gesetzt");
				return true;
			}

			if (args[0].equalsIgnoreCase("0")) {

				player.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage("§7Der Gamemode von "+ player.getName() +" wurde auf §bSurvival §7gesetzt");
				return true;
			}

		} else if(args.length == 1) {
			Player player = (Player) sender;

			if (args[0].equalsIgnoreCase("1")) {

				player.setGameMode(GameMode.CREATIVE);
				player.sendMessage("§7Dein Gamemode wurde auf §bCreative §7gesetzt");
				return true;
			}

			if (args[0].equalsIgnoreCase("0")) {

				player.setGameMode(GameMode.SURVIVAL);
				player.sendMessage("§7Dein Gamemode wurde auf §bSurvival §7gesetzt");
				return true;
			}
		}



		// Falls der Befehl nicht erkannt wurde, gib eine entsprechende Nachricht aus
		sender.sendMessage("§7Unbekannter Befehl. Nutze §b/gm 1 §7um den Gamemode auf Creative zu setzen");
		return false;
	}

}

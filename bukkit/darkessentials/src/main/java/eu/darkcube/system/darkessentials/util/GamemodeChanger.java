package eu.darkcube.system.darkessentials.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.darkessentials.Main;

public class GamemodeChanger {
	
	public static void changeGamemode(int gamemode, CommandSender sender,
					Collection<String> targetNames) {
		String gmString = "Survival";
		switch (gamemode) {
		case 0:
		break;
		case 1:
			gmString = "Creative";
		break;
		case 2:
			gmString = "Adventure";
		break;
		case 3:
			gmString = "Spectator";
		break;
		default:
			Main.getInstance().sendMessage(Main.cFail()
							+ "Du musst einen Gamemode angeben!", sender);
			return;
		}
		int count = 0;
		if (targetNames.size() == 0) {
			if (sender instanceof Player) {
				targetNames.add(((Player) sender).getName());
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return;
			}
		}
		Set<String> unresolvedNames = new HashSet<>();
		if (targetNames.contains(((Player) sender).getName())) {
			if (((Player) sender).getGameMode().equals(GameMode.valueOf(gmString.toUpperCase())))
				Main.getInstance().sendMessage(Main.cFail()
								+ "Du bist bereits in diesem Gamemode!", sender);
		}
		for (String playerName : targetNames) {
			if (Bukkit.getPlayer(playerName) != null) {
				Bukkit.getPlayer(playerName).setGameMode(GameMode.valueOf(gmString.toUpperCase()));
				count++;
			} else {
				unresolvedNames.add(playerName);
			}
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		if (targetNames.size() == 1 && targetNames.contains(((Player) sender).getName())
						&& unresolvedNames.isEmpty()) {
			Main.getInstance().sendMessage(new StringBuilder().append(Main.cConfirm()).append("Gamemode von ").append(Main.cValue()).append(count).append(Main.cConfirm()).append(" Spielern auf ").append(gmString).append(Main.cConfirm()).append(" gesetzt").toString(), sender);
		}
	}

	public static void changeGamemode(int gamemode, CommandSender sender, String... targetNames) {
		changeGamemode(gamemode, sender, EssentialCollections.asSet(targetNames));
	}

	public static void changeGamemode(int gamemode, CommandSender sender, Player target) {
		changeGamemode(gamemode, sender, target.getName());
	}
}
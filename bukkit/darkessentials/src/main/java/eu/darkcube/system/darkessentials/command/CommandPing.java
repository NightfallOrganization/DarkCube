package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandPing extends Command {

	public CommandPing() {
		super(Main.getInstance(), "ping", new Command[0], "Zeigt den Ping eines Spielers.",
				new Argument[] { new Argument("Spieler", "Der Spieler, dessen Ping gezeit werden soll.", false) });
		setAliases("d_ping");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length >= 1) {
			for (String playerName : args) {
				if (Bukkit.getPlayer(playerName) != null) {
					players.add(Bukkit.getPlayer(playerName));
				} else {
					unresolvedNames.add(playerName);
				}
			}
		} else if (sender instanceof Player) {
			players.add((Player) sender);
		} else {
			Main.sendMessagePlayernameRequired(sender);
			return true;
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		for (Player current : players) {
			try {
				Object player = current.getClass().getMethod("getHandle").invoke(current);
				int ping = (int) player.getClass().getField("ping").get(player);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(Main.cConfirm()).append(current.getName()).append("'s Ping beträgt ").append(Main.cValue()).append(ping)
						.append("ms");
				Main.getInstance().sendMessage(stringBuilder.toString(), sender);
			} catch (Exception e) {

			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if(args.length!=0) 
			return Main.getPlayersStartWith(args);
		return new ArrayList<>();
	}
}

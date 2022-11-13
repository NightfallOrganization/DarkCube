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
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandSpeed extends Command {

	public CommandSpeed() {
		super(Main.getInstance(), "speed", new Command[0], "Stellt deine Geschwindigkeit um.",
				new Argument[] {
						new Argument("walk|fly", "Ob deine Lauf- oder Fluggeschwindigkeit geändert wird.", false),
						new Argument("Wert", "Der Wert auf den deine Geschwindigkeit gesetzt wird."),
						new Argument("Spieler", "Der Spieler, dessen Geschwindigkeit geändert wird.", false) });
		setAliases("d_speed");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		float value = 1;
		int valuePos = 0;
		int count = 0;
		boolean walk = true;
		boolean fly = true;
		if (args.length == 0) {
			return false;
		}
		if (args[0].equalsIgnoreCase("walk")) {
			fly = false;
			valuePos = 1;
			args[0] = "%processed%";
		} else if (args[0].equalsIgnoreCase("fly")) {
			walk = false;
			valuePos = 1;
			args[0] = "%processed%";
		}
		if (valuePos == 0 || (valuePos == 1 && args.length >= 1)) {
			try {
				value = Float.parseFloat(args[valuePos]);
				args[valuePos] = "%processed%";
			} catch (NumberFormatException e) {
				Main.getInstance()
						.sendMessage(new StringBuilder().append(Main.cFail()).append("Du musst eine Zahl zwischen")
								.append(Main.cValue()).append("0 und 10").append(Main.cFail()).append(" angeben!")
								.toString(), sender);
				return true;
			}
		}
		Set<String> unresolvedNames = new HashSet<>();
		for (String playerName : args) {
			if (!playerName.equals("%processed%")) {
				if (Bukkit.getPlayer(playerName) != null) {
					players.add(Bukkit.getPlayer(playerName));
				} else {
					unresolvedNames.add(playerName);
				}
			}
		}
		if (players.isEmpty() && sender instanceof Player) {
			players.add((Player) sender);
		} else {
			Main.sendMessagePlayernameRequired(sender);
			return true;
		}
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		for (Player current : players) {
			float newValue;
			if (value <= 10 && value >= 0) {
				newValue = value / 10;
			} else {
				Main.getInstance()
						.sendMessage(new StringBuilder().append(Main.cFail()).append("Du musst eine Zahl zwischen")
								.append(Main.cValue()).append("0 und 10").append(Main.cFail()).append(" angeben!")
								.toString(), sender);
				return true;
			}
			if (walk && fly) {
				if (current.isFlying()) {
					walk = false;
				} else {
					fly = false;
				}
			}
			if (walk) {
				if (newValue != 0) {
					current.setWalkSpeed((float) (newValue + 0.1));
				} else {
					current.setWalkSpeed(0);
				}
				Main.getInstance().sendMessage("§aDeine Laufgeschwindigkeit wurde auf §5" + value + " geändert!",
						current);
			}
			if (fly) {
				current.setFlySpeed(newValue);
				Main.getInstance().sendMessage("§aDeine Fluggeschwindigkeit wurde auf §5" + value + " geändert!",
						current);
			}
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			Main.getInstance().sendMessage(
					"§aGeschwindigkeit von §5" + count + " §aSpielern auf §5" + value + " geändert!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(EssentialCollections.asList("walk", "fly"), args[0]);
		}
		if (args.length > 1) {
			return Main.getPlayersStartWith(args);
		}
		return new ArrayList<>();
	}

}

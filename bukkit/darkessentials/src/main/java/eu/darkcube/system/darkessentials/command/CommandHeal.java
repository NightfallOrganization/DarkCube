package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandHeal extends Command {

	public CommandHeal() {
		super(Main.getInstance(), "heal", new Command[0], "Heilt den angegebenen Spieler vollständig.",
				new Argument("Spieler", "Der zu heilende Spieler", false));
		setAliases("d_heal");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		List<String> argsList = Arrays.asList(args);
		Set<Player> players = new HashSet<>();
		int count = 0;
		if (sender instanceof Player && args.length == 0) {
			players.add((Player) sender);
		} else if (args.length != 0) {
			if (argsList.contains("all") && args.length <= 1) {
				players.addAll(Bukkit.getOnlinePlayers());
			} else {
				for (String current : args) {
					if (Bukkit.getPlayer(current) != null) {
						players.add(Bukkit.getPlayer(current));
					}
				}
			}
		} else {
			Main.sendMessagePlayernameRequired(sender);
			return true;
		}
		for (Player current : players) {
			removeAllNegativePotions(current);
			current.setHealth(current.getMaxHealth());
			Main.getInstance().sendMessage(Main.cConfirm() + "Du wurdest geheilt!", current);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			Main.getInstance().sendMessage(Main.cValue() + count + Main.cConfirm() + " Spieler geheilt!", sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		List<String> argsList = Arrays.asList(args);
		if (args.length != 0) {
			if (argsList.contains("all")) {
				return list;
			}
			if (args.length == 1 && "all".startsWith(args[0].toLowerCase())) {
				list.add("all");
			}
			if (!argsList.contains("all")) {
				list.addAll(Main.getPlayersStartWith(args));
			}
		}
		return list;
	}

	private static enum NegativeEffects {
		CONFUSION, HARM, HUNGER, POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER;
	}

	private void removeAllNegativePotions(Player player) {
		for (PotionEffect effects : player.getActivePotionEffects()) {
			for (NegativeEffects bad : NegativeEffects.values()) {
				if (effects.getType().getName().equalsIgnoreCase(bad.name())) {
					player.removePotionEffect(effects.getType());
				}
			}
		}
	}
}

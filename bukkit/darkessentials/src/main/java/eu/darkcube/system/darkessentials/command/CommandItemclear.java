package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.NumbzUtils;

public class CommandItemclear extends Command {

	public CommandItemclear() {
		super(Main.getInstance(), "itemclear", new Command[0], "Löscht gedroppte Items.",
				new Argument[] { new Argument("Radius", "Der Radius, in dem Items gelöscht werden sollten.", false),
						new Argument("Spieler", "Der Spieler, um den herum Items gelöscht werden sollen.", false),
						new Argument("Welt", "Die Welt, in der Items gelöscht werden sollen.", false) });
		setAliases("d_itemclear");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		int radius = Integer.MAX_VALUE;
		Set<Player> players = new HashSet<>();
		Set<World> worlds = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length != 0) {
			try {
				radius = Integer.parseInt(args[0]);
				args[0] = "%processed%";
			} catch (Exception e) {
			}
			for (String name : args) {
				if (!name.equals("%processed%")) {
					if (Bukkit.getPlayer(name) != null) {
						players.add(Bukkit.getPlayer(name));
					} else if (Bukkit.getWorld(name) != null) {
						worlds.add(Bukkit.getWorld(name));
					} else {
						unresolvedNames.add(name);
					}
				}
			}
			if (!players.isEmpty() && !worlds.isEmpty()) {
				Main.getInstance()
						.sendMessage(Main.cFail() + "Du kannst nicht gleichzeitig Welten und Spieler angeben!", sender);
				return true;
			}
			if (radius == Integer.MAX_VALUE && !players.isEmpty()) {
				Main.getInstance().sendMessage(
						Main.cFail() + "Du musst einen Radius angeben, wenn du einen Spieler angibst!", sender);
				return true;
			}
			if (radius != Integer.MAX_VALUE && players.isEmpty()) {
				Main.getInstance().sendMessage(
						Main.cFail() + "Du musst einen Spieler angeben, wenn du einen Radius angibst!", sender);
				return true;
			}
			if (radius != Integer.MAX_VALUE && !worlds.isEmpty()) {
				Main.getInstance().sendMessage(
						Main.cFail() + "Du kannst nicht gleichzeitig eine Welt und einen Radius angeben!", sender);
				return true;
			}
		}

		if (unresolvedNames.size() != 0) {
			StringBuilder sb = new StringBuilder();
			if (unresolvedNames.size() > 1) {
				sb.append(Main.cFail() + "Die Spieler oder Welten " + ChatColor.GRAY + "\"");
			} else {
				sb.append(Main.cFail() + "Der Spieler oder die Welt " + ChatColor.GRAY + "\"");
			}
			for (String name : unresolvedNames) {
				sb.append(Main.cValue() + name + ChatColor.GRAY + "\", \"");
			}
			if (unresolvedNames.size() > 1) {
				Main.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + Main.cFail()
						+ " konnten nicht gefunden werden!", sender);
			} else {
				Main.getInstance().sendMessage(sb.toString().substring(0, sb.toString().length() - 3) + Main.cFail()
						+ " konnte nicht gefunden werden!", sender);
			}
		}
		int count = 0;
		if (!players.isEmpty()) {
			for (Player player : players) {
				for (Entity entity : player.getWorld().getEntities()) {
					if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
						if (NumbzUtils.getDistance(player, entity) <= radius) {
							entity.remove();
							count++;
						}
					}
				}
			}
		} else {
			worlds.addAll(Bukkit.getWorlds());
		}
		if (!worlds.isEmpty()) {
			for (World world : Bukkit.getWorlds()) {
				for (Entity entity : world.getEntities()) {
					if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
						entity.remove();
						count++;
					}
				}
			}
		}

		Main.getInstance().sendMessage(new StringBuilder(Main.cValue()).append(count).append(Main.cConfirm())
				.append(" Items wurden gelöscht!").toString(), Bukkit.getOnlinePlayers());

		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), EssentialCollections.asList(args),
					args[args.length - 1]);
		}
		if (args.length > 1) {
			try {
				Integer.parseInt(args[0]);
				return Main.getPlayersStartWith(args);
			} catch (Exception e) {
				return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), EssentialCollections.asList(args),
						args[args.length - 1]);
			}
		}
		return new ArrayList<>();
	}
}

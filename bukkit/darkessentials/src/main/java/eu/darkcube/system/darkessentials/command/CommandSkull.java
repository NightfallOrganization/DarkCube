/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandSkull extends Command {

	public CommandSkull() {
		super(Main.getInstance(), "skull", new Command[0], "Gibt dir den Kopf eines Spielers.",
				new Argument[] { new Argument("Spieler|Mob|me", "Der Spieler oder Mob, dessen Kopf du haben möchtest."),
						new Argument("Spieler", "Der Spieler, der den Kopf bekommen soll.") });
		setAliases("d_head", "head");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			Main.getInstance().sendMessage(Main.cFail() + "Du musst einen Spielernamen angeben!", sender);
			return true;
		}
		if (args.length == 1 && !(sender instanceof Player)) {
			Main.sendMessagePlayernameRequired(sender);
			return true;
		}
		boolean playerHead = false;
		String skullOwner = "";
		switch (args[0].toLowerCase()) {
		case "zombie":
			skullOwner = "ZOMBIE";
			break;
		case "skeleton":
			skullOwner = "SKELETON";
			break;
		case "witherskeleton":
		case "wither_skeleton":
		case "wither":
			skullOwner = "WITHER";
			break;
		case "creeper":
			skullOwner = "CREEPER";
			break;
		case "steve":
			skullOwner = "steve";
			break;
		case "god":
			skullOwner = "Notch";
			playerHead = true;
			break;
		case "me":
			if (sender instanceof Player) {
				skullOwner = ((Player) sender).getName();
				playerHead = true;
				break;
			}
			Main.getInstance().sendMessage(
					Main.cFail() + "Du bist kein Spieler, deshalb musst du einen weiteren Spielernamen angeben!",
					sender);
			return true;
		default:
			skullOwner = args[0];
			playerHead = true;
		}
		args[0] = "%processed%";
		Set<Player> players = new HashSet<>();
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
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			players.add((Player) sender);
		}
		ItemStack skullItem;
		SkullMeta meta = null;
		if (playerHead) {
			skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			meta = (SkullMeta) skullItem.getItemMeta();
			meta.setOwner(skullOwner);
			if (Bukkit.getPlayer(skullOwner) != null) {
				skullOwner = Bukkit.getPlayer(skullOwner).getName();
			}
			meta.setDisplayName(ChatColor.RESET + "Kopf von " + ChatColor.GOLD + skullOwner);
		} else if (skullOwner.equals("steve")) {
			skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			meta = (SkullMeta) skullItem.getItemMeta();
			meta.setDisplayName(ChatColor.RESET + "Kopf von " + ChatColor.GOLD + "Steve");
		} else {
			skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.valueOf(skullOwner).ordinal());
			meta = (SkullMeta) skullItem.getItemMeta();
			switch (skullOwner) {
			case "ZOMBIE":
				meta.setDisplayName(ChatColor.GREEN + "Zombie" + ChatColor.RESET + "-Kopf");
				break;
			case "SKELETON":
				meta.setDisplayName(ChatColor.GRAY + "Skelett" + ChatColor.RESET + "-Kopf");
				break;
			case "WITHER":
				meta.setDisplayName(ChatColor.DARK_GRAY + "Witherskelett" + ChatColor.RESET + "-Kopf");
				break;
			case "CREEPER":
				meta.setDisplayName(ChatColor.DARK_GREEN + "Creeper" + ChatColor.RESET + "-Kopf");
				meta.setLore(
						EssentialCollections.asList(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Aww man"));
				break;
			}
		}
		if (sender instanceof Player && skullOwner.equalsIgnoreCase("katharii")) {
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.CAT_MEOW, 20, 1);
		}
		skullItem.setItemMeta(meta);
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		int count = 0;
		for (Player current : players) {
			current.getInventory().addItem(skullItem);
			Main.getInstance().sendMessage(Main.cConfirm() + "Du hast einen Kopf erhalten!", current);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender)))
			Main.getInstance().sendMessage(new StringBuilder(Main.cValue()).append(count).append(Main.cConfirm())
					.append(" Spielern einen Kopf gegeben.").toString(), sender);

		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length != 0) {
			if (args.length == 1) {
				list.addAll(Main.getPlayersStartWith(args));
				for (String s : EssentialCollections.asList("creeper", "zombie", "skeleton", "witherskeleton")) {
					if (s.startsWith(args[0]))
						list.add(s);
				}
			} else {
				args[0] = "%processed%";
				list.addAll(Main.getPlayersStartWith(args));
			}
		}
		return list;
	}
}

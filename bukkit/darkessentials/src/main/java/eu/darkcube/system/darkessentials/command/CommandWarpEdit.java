/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.darkessentials.util.WarpPoint;
import net.md_5.bungee.api.ChatColor;

public class CommandWarpEdit extends Command {

	Set<WarpPoint> warps = new HashSet<>();
	Map<CommandSender, String> warpDeleteComfirmation = new HashMap<>();

	public CommandWarpEdit() {
		super(DarkEssentials.getInstance(), "warpedit", new Command[0], "Bearbeitet Warp-Punkte.",
				new Argument("create|delete|edit|info", "Was gemacht werden soll."),
				new Argument("Name", "Der Name des Warp-Punktes, der bearbeitet werden soll."),
				new Argument("setIcon|setPosition|setName|setEnabled", "Nur bei Edit: Was abgeändert werden soll.",
						false),
				new Argument("Wert", "Nur bei Edit: Wie es abgeändert werden soll.", false));
		setAliases("d_warpedit");
		warps = DarkEssentials.updateWarps();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0 || (args.length == 1 && !args[0].equalsIgnoreCase("confirm"))
				|| (args.length > 2 && !args[0].equalsIgnoreCase("edit"))
				|| (args.length < 3 && args[0].equalsIgnoreCase("edit"))
				|| (args.length < 4 && args[0].equalsIgnoreCase("edit") && !args[2].equalsIgnoreCase("setPosition")))
			return false;

		warps = DarkEssentials.updateWarps();

		switch (args[0].toLowerCase()) {
		case "create":
			if (getWarp(args[1]) == null) {
				if (!(sender instanceof Player)) {
					// warps.add(new WarpPoint(args[1], null, null, false));
					warps.add(new WarpPoint(args[1]));
					DarkEssentials.getInstance().sendMessage("Warp-Punkt erstellt. Folgende Parameter sind noch undefiniert:",
							sender);
					DarkEssentials.getInstance().sendMessage("- Position", sender);
					DarkEssentials.getInstance().sendMessage("- Icon", sender);
				} else {
					warps.add(new WarpPoint(args[1], ((Player) sender).getLocation(), null, false));
					DarkEssentials.getInstance().sendMessage(
							DarkEssentials.colorConfirm + "Warp-Punkt erstellt. Folgende Parameter sind noch undefiniert:",
							sender);
					DarkEssentials.getInstance().sendMessage(ChatColor.GRAY + "-" + DarkEssentials.colorValue + " Icon", sender);
				}
			} else {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ").append(
												DarkEssentials.colorValue)
								.append(args[1]).append(DarkEssentials.colorFail).append(" existiert bereits!").toString(),
								sender);
			}
			break;
		case "delete":
			if (getWarp(args[1]) != null) {
				warpDeleteComfirmation.put(sender, args[1]);
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Der Warp-Punkt ")
								.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName()).append(
										DarkEssentials.colorConfirm)
								.append(" wird gelöscht!").toString(), sender);
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Verwende ").append(
										DarkEssentials.colorValue)
								.append("/warpedit confirm").append(DarkEssentials.colorConfirm).append(" zum Bestätigen.")
								.toString(), sender);
			} else {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ").append(
												DarkEssentials.colorValue)
								.append(args[1]).append(DarkEssentials.colorFail).append(" wurde nicht gefunden!").toString(),
								sender);
			}
			break;
		case "edit":
			if (getWarp(args[1]) != null) {
				switch (args[2].toLowerCase()) {
				case "seticon":
					Material material = Material.STONE;
					short damage = 0;
					if (args[3].equalsIgnoreCase("hand") || args[3].equalsIgnoreCase("this")) {
						if (!(sender instanceof Player))
							return DarkEssentials.sendMessageAndReturnTrue(DarkEssentials.colorFail
									+ "Du bist kein Spieler, deshalb kannst du dieses Argument nicht verwenden!",
									sender);
						if (((Player) sender).getItemInHand() == null
								|| ((Player) sender).getItemInHand() == new ItemStack(Material.AIR))
							return DarkEssentials.sendMessageAndReturnTrue(
									DarkEssentials.colorFail + "Deine Hand ist leer!", sender);
						material = ((Player) sender).getItemInHand().getType();
						damage = ((Player) sender).getItemInHand().getDurability();
					} else {
						try {
							material = Material.valueOf(args[3].toUpperCase(Locale.ENGLISH));
						} catch (Exception e) {
							DarkEssentials.getInstance()
									.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Das Item ")
											.append(DarkEssentials.colorValue).append(args[3]).append(
													DarkEssentials.colorFail)
											.append(" wurde nicht gefunden!").toString(), sender);
							return true;
						}
						if (args.length == 5) {
							try {
								damage = Short.parseShort(args[4]);
							} catch (Exception e) {
								return DarkEssentials.sendMessageAndReturnTrue(
										DarkEssentials.colorFail + "Ungültige Zahl!", sender);
							}
						} else if (args.length > 5)
							return DarkEssentials.sendMessageAndReturnTrue(
									DarkEssentials.colorFail + "Ungültige Zahl!", sender);
					}
					DarkEssentials.getInstance().sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Icon für Warp-Punkt ")
							.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName()).append(
									DarkEssentials.colorConfirm)
							.append(" erfolgreich geändert.").toString(), sender);
					getWarp(args[1]).setIcon(material.toString() + ":" + damage);
					break;
				case "setposition":
					double x = 0, y = 0, z = 0;
					if (args.length >= 6)
						try {
							x = Integer.parseInt(args[3]);
							y = Integer.parseInt(args[4]);
							z = Integer.parseInt(args[5]);
						} catch (Exception e) {
							DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail + "Ungültige Zahl!", sender);
							return true;
						}
					Location loc = new Location(null, x, y, z);

					if (args.length < 4)
						if (sender instanceof Player) {
							loc = ((Player) sender).getLocation();
						} else {
							DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail
									+ "Du bist kein Spieler, deshalb musst du Koordianten und eine Welt angeben!",
									sender);
							return true;
						}
					else if (args.length == 4)
						if (sender instanceof Player) {
							if (args[3].equalsIgnoreCase("true")) {
								loc = ((Player) sender).getLocation();
								loc.setX(loc.getBlockX() + 0.5);
								loc.setY(loc.getBlockY());
								loc.setZ(loc.getBlockZ() + 0.5);
								loc.setWorld(((Player) sender).getWorld());
								loc.setPitch(10 * Math.round(((Player) sender).getLocation().getPitch() / 10));
								loc.setYaw(10 * Math.round(((Player) sender).getLocation().getYaw() / 10));
								((Player) sender).teleport(loc);
							} else if (args[3].equalsIgnoreCase("false"))
								loc = ((Player) sender).getLocation();
							else {
								DarkEssentials.getInstance()
										.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Du kannst nur ")
												.append(DarkEssentials.colorValue).append("true").append(
														DarkEssentials.colorFail)
												.append(" oder ").append(DarkEssentials.colorValue).append("false")
												.append(DarkEssentials.colorFail).append(" angeben!").toString(), sender);
								return true;
							}
						} else {
							DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail
									+ "Du bist kein Spieler, deshalb musst du Koordianten und eine Welt angeben!",
									sender);
							return true;
						}
					else if (args.length < 6)
						return false;
					else if (args.length == 6) {
						if (sender instanceof Player) {
							loc.setWorld(((Player) sender).getWorld());
						} else if (getWarp(args[1]).getLocation() != null) {
							loc.setWorld(getWarp(args[1]).getLocation().getWorld());
						} else {
							DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail + "Du musst eine Welt angeben!", sender);
							return true;
						}
					} else if (args.length == 7) {
						if (Bukkit.getWorld(args[6]) != null)
							loc.setWorld(Bukkit.getWorld(args[6]));
						else {
							DarkEssentials.getInstance()
									.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Die Welt ")
											.append(DarkEssentials.colorValue).append(args[6]).append(
													DarkEssentials.colorFail)
											.append(" wurde nicht gefunden!").toString(), sender);
							return true;
						}
					} else if (args.length > 7)
						return false;

					getWarp(args[1]).setLocation(loc);
					DarkEssentials.getInstance()
							.sendMessage(
									new StringBuilder(DarkEssentials.colorConfirm).append("Position für Warp-Punkt ")
											.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName())
											.append(DarkEssentials.colorConfirm).append(" erfolgreich geändert.").toString(),
									sender);
					break;
				case "setname":
					if (getWarp(args[3]) == null) {
						DarkEssentials.getInstance().sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Warp-Punkt ")
								.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName()).append(
										DarkEssentials.colorConfirm)
								.append(" erfolgreich umbenannt.").toString(), sender);
						getWarp(args[1]).setName(args[3]);
					} else {
						DarkEssentials.getInstance()
								.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ")
										.append(DarkEssentials.colorValue).append(args[3]).append(
												DarkEssentials.colorFail)
										.append(" existiert bereits!").toString(), sender);
					}
					break;
				case "setenabled":
					if (args[3].equalsIgnoreCase("true")) {
						WarpPoint warp = getWarp(args[1]);
						if (warp.isValid()) {
							DarkEssentials.getInstance()
									.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Warp-Punkt ")
											.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName())
											.append(DarkEssentials.colorConfirm).append(" erfolgreich aktiviert.").toString(),
											sender);
							warp.setEnabled(true);
						} else {
							DarkEssentials.getInstance().sendMessage(
									DarkEssentials.colorFail + "Alle Parameter des Warp-Punktes müssen definiert sein!", sender);
						}
					} else if (args[3].equalsIgnoreCase("false")) {
						DarkEssentials.getInstance()
								.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Warp-Punkt ")
										.append(DarkEssentials.colorValue).append(getWarp(args[1]).getName())
										.append(DarkEssentials.colorConfirm).append(" erfolgreich deaktiviert.").toString(),
										sender);
						getWarp(args[1]).setEnabled(false);
					} else {
						DarkEssentials.getInstance()
								.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Du musst ")
										.append(DarkEssentials.colorValue).append("true").append(
												DarkEssentials.colorFail).append(" oder ")
										.append(DarkEssentials.colorValue).append("false").append(
												DarkEssentials.colorFail)
										.append(" angeben!").toString(), sender);
					}
					break;
				default:
					return false;
				}
			} else {
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ").append(
												DarkEssentials.colorValue)
								.append(args[1]).append(DarkEssentials.colorFail).append(" wurde nicht gefunden!").toString(),
								sender);
			}
			break;
		case "info":
			if (getWarp(args[1]) != null) {
				WarpPoint warp = getWarp(args[1]);
				warp.isValid();
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Informationen zum Warp-Punkt ")
								.append(DarkEssentials.colorValue).append(warp.getName()).append(
										DarkEssentials.colorConfirm).append(":")
								.toString(), sender);
				if (warp.getLocation() != null) {
					Location loc = warp.getLocation();
					DarkEssentials.getInstance().sendMessage(new StringBuilder(ChatColor.GRAY.toString()).append("- Position: ")
							.append(DarkEssentials.colorValue).append(loc.getBlockX()).append(ChatColor.DARK_GRAY).append(", ")
							.append(DarkEssentials.colorValue).append(loc.getBlockY()).append(ChatColor.DARK_GRAY).append(", ")
							.append(DarkEssentials.colorValue).append(loc.getBlockZ()).append(ChatColor.DARK_GRAY).append(", ")
							.append(DarkEssentials.colorValue).append(loc.getWorld().getName()).toString(), sender);
				} else {
					DarkEssentials.getInstance().sendMessage(new StringBuilder(ChatColor.GRAY.toString()).append("- Position: ")
							.append(DarkEssentials.colorValue).append("undefiniert").toString(), sender);
				}
				if (warp.getIcon() != null) {
					DarkEssentials.getInstance().sendMessage(new StringBuilder(ChatColor.GRAY.toString()).append("- Icon: ")
							.append(DarkEssentials.colorValue).append(warp.getIcon()).toString(), sender);
				} else {
					DarkEssentials.getInstance().sendMessage(new StringBuilder(ChatColor.GRAY.toString()).append("- Icon: ")
							.append(DarkEssentials.colorValue).append("undefiniert").toString(), sender);
				}
				DarkEssentials.getInstance().sendMessage(new StringBuilder(ChatColor.GRAY.toString()).append("- Aktiviert: ")
						.append(DarkEssentials.colorValue).append(warp.getEnabled()).toString(), sender);
			} else
				DarkEssentials.getInstance()
						.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ").append(
												DarkEssentials.colorValue)
								.append(args[1]).append(DarkEssentials.colorFail).append(" wurde nicht gefunden!").toString(),
								sender);
			break;
		case "confirm":
			if (warpDeleteComfirmation.containsKey(sender)) {
				if (warps.contains(getWarp(warpDeleteComfirmation.get(sender)))) {
					warps.remove(getWarp(warpDeleteComfirmation.get(sender)));
					DarkEssentials.getInstance()
							.sendMessage(new StringBuilder(DarkEssentials.colorConfirm).append("Der Warp-Punkt ")
									.append(DarkEssentials.colorValue).append(warpDeleteComfirmation.get(sender))
									.append(DarkEssentials.colorConfirm).append(" wurde gelöscht!").toString(), sender);
				} else
					DarkEssentials.getInstance()
							.sendMessage(new StringBuilder(DarkEssentials.colorFail).append("Der Warp-Punkt ")
									.append(DarkEssentials.colorValue).append(warpDeleteComfirmation.get(sender))
									.append(DarkEssentials.colorFail).append(" wurde bereits gelöscht!").toString(), sender);
				warpDeleteComfirmation.remove(sender);
			} else
				DarkEssentials.getInstance().sendMessage(DarkEssentials.colorFail + "Nichts zum Bestätigen!", sender);
			break;
		default:
			return false;
		}
		DarkEssentials.config.set("warps", new Gson().toJson(warps));
		return true;
	}

	private WarpPoint getWarp(String name) {
		for (WarpPoint warp : warps) {
			if (warp.getName().toLowerCase().equals(name.toLowerCase()))
				return warp;
		}
		return null;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return EssentialCollections
					.toSortedStringList(EssentialCollections.asList("create", "delete", "edit", "info"), args[0]);
		if (args.length == 2)
			switch (args[0].toLowerCase()) {
			case "delete":
			case "edit":
			case "info":
				Set<String> enabledWarps = new HashSet<>();
				for (WarpPoint w : DarkEssentials.updateWarps())
					enabledWarps.add(w.getName());
				return EssentialCollections.toSortedStringList(enabledWarps, args[1]);
			}
		if (args.length == 3)
			if (args[0].equalsIgnoreCase("edit"))
				return EssentialCollections.toSortedStringList(
						EssentialCollections.asList("setIcon", "setPosition", "setName", "setEnabled"), args[2]);
		if (args.length == 4)
			if (args[0].equalsIgnoreCase("edit"))
				switch (args[2].toLowerCase()) {
				case "seticon":
					return EssentialCollections.toSortedStringList(Material.values(), args[3]);
				case "setposition":
				case "setenabled":
					return EssentialCollections.toSortedStringList(EssentialCollections.asList("true", "false"),
							args[3]);
				}
		if (args.length == 7)
			if (args[0].equalsIgnoreCase("edit") && args[2].equalsIgnoreCase("setPosition"))
				return EssentialCollections.toSortedStringList(Bukkit.getWorlds(), args[6]);
		return new ArrayList<>();
	}

}

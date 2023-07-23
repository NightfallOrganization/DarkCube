/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ColorUtil;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.commandapi.v3.arguments.StringArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import static eu.darkcube.system.commandapi.v3.Commands.argument;

public class CommandCreateTeam extends WBCommandExecutor {
	public CommandCreateTeam() {
		super("createTeam", b -> b.then(argument("team", StringArgument.string(
				Arrays.asList(SupportedColors.values()).stream().map(SupportedColors::name)
						.filter(dnk -> {
							TeamType t = TeamType.byDisplayNameKey(dnk);
							return t == null || t.isDeleted();
						}).toArray(String[]::new))).then(
				argument("weight", IntegerArgumentType.integer()).then(argument("woolColor",
						EnumArgument.enumArgument(DyeColor.values(),
								d -> new String[] {d.name().toLowerCase()})).then(
						argument("nameColor",
								EnumArgument.enumArgument(SupportedColors.values())).then(
								Commands.argument("maxPlayers", IntegerArgumentType.integer(0))
										.executes(ctx -> {
											String teamDNK = StringArgument.getString(ctx, "team");
											TeamType teamType = TeamType.byDisplayNameKey(teamDNK);
											if (teamType != null && !teamType.isDeleted()) {
												ctx.getSource().sendMessage(Component.text("Es "
														+ "existiert bereits ein Team mit dem "
														+ "Namen " + teamDNK));
												return 0;
											}
											int weight =
													IntegerArgumentType.getInteger(ctx, "weight");
											for (TeamType type : TeamType.values()) {
												if (type.getWeight() == weight) {
													ctx.getSource().sendMessage(Component.text(
															"Ein Team hat bereits die Sortierung "
																	+ weight));
												}
											}
											DyeColor woolColor =
													EnumArgument.getEnumArgument(ctx, "woolColor",
															DyeColor.class);
											ChatColor nameColor =
													EnumArgument.getEnumArgument(ctx, "nameColor",
															ChatColor.class);
											int maxPlayers = IntegerArgumentType.getInteger(ctx,
													"maxPlayers");
											teamType = new TeamType(teamDNK, weight,
													ColorUtil.byDyeColor(woolColor), nameColor,
													false, maxPlayers);
											teamType.save();
											ctx.getSource().sendMessage(Component.text(
															"Du hast ein neues Team mit dem Namen '"
																	+ teamDNK + "', der Sortierung "
																	+ weight + ", der Wollfarbe "
																	+ woolColor.name() + ", der Namenfarbe "
																	+ nameColor.name()
																	+ " und der Maximalen Spieleranzahl "
																	+ maxPlayers + " erstellt!")
													.append(Component.newline()
															.append(Component.text(
																	"Bedenke, dass du den "
																			+ "Server neustarten musst damit das "
																			+ "Team erkannt wird!"))));
											return 0;
										})))))));
	}

	public enum SupportedColors {
		RED, BLUE, BLACK, GRAY, GREEN, WHITE, PURPLE, YELLOW
	}

	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		switch (args.length) {
	//			case 1:
	//				return Arrays.toSortedStringList(
	//						Arrays.asList("RED", "BLUE", "BLACK", "GRAY", "GREEN", "WHITE", "PURPLE",
	//								"YELLOW").stream().filter(id -> {
	//							for (TeamType t : TeamType.values()) {
	//								if (t.getDisplayNameKey() == id) {
	//									return false;
	//								}
	//							}
	//							return true;
	//						}).collect(Collectors.toList()), args[0]);
	//			case 2:
	//				return Arrays.toSortedStringList(
	//						Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15).stream()
	//								.filter(id -> {
	//									for (TeamType t : TeamType.values()) {
	//										if (t.getWeight() == id) {
	//											return false;
	//										}
	//									}
	//									return true;
	//								}).collect(Collectors.toList()), args[1]);
	//			case 3:
	//				return Arrays.toSortedStringList(DyeColor.values(), args[2].toLowerCase());
	//			case 4:
	//				return Arrays.toSortedStringList(ChatColor.values(), args[3].toLowerCase());
	//			case 5:
	//				return Arrays.toSortedStringList(Arrays.asList(1, 2, 4, 6, 8, 40), args[4]);
	//			default:
	//				return super.onTabComplete(args);
	//		}
	//	}
	//
	//	@SuppressWarnings("deprecation")
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 5) {
	//			TeamType type = TeamType.byDisplayNameKey(args[0]);
	//			if (type != null && !type.isDeleted()) {
	//				sender.sendMessage(
	//						"§cEs existiert bereits ein Team mit dem Namen '" + type.getDisplayNameKey()
	//								+ "'!");
	//				return true;
	//			}
	//			Integer weight = null;
	//			try {
	//				weight = Integer.valueOf(args[1]);
	//				if (weight < 0)
	//					weight = null;
	//			} catch (Exception ex) {
	//			}
	//			if (weight == null) {
	//				sender.sendMessage("§cBitte gib als Sortierung eine positive Zahl an!");
	//				return true;
	//			}
	//			for (TeamType t : TeamType.values()) {
	//				if (t.getWeight() == weight) {
	//					sender.sendMessage(
	//							"§cDiese Sortierung ist bereits dem Team " + t.getDisplayNameKey()
	//									+ " vergeben!");
	//					return true;
	//				}
	//			}
	//
	//			DyeColor woolColor = DyeColor.getByData(ColorUtil.byDyeColor(args[2]));
	//			if (woolColor == null) {
	//				sender.sendMessage("§cBitte gib eine gültige Wollfarbe an!");
	//				return true;
	//			}
	//			ChatColor chatColor = ChatColor.getByChar(ColorUtil.byChatColor(args[3]));
	//			if (chatColor == null) {
	//				sender.sendMessage("§cBitte gib eine gültige Namenfarbe an!");
	//				return true;
	//			}
	//			Integer maxPlayers = null;
	//			try {
	//				maxPlayers = Integer.valueOf(args[4]);
	//				if (maxPlayers <= 0)
	//					maxPlayers = null;
	//			} catch (Exception ex) {
	//			}
	//			if (maxPlayers == null) {
	//				sender.sendMessage("§cBitte gib als Maximale Spieleranzahl eine positive Zahl an!");
	//				return true;
	//			}
	//			type = new TeamType(args[0], weight, ColorUtil.byDyeColor(woolColor), chatColor, false,
	//					maxPlayers);
	//			type.save();
	//			sender.sendMessage(
	//					"§7Du hast ein neues Team mit dem Namen '" + args[0] + "', der Sortierung "
	//							+ weight + ", der Wollfarbe " + woolColor.name() + ", der Namenfarbe "
	//							+ chatColor.name() + " und der Maximalen Spieleranzahl " + maxPlayers
	//							+ " erstellt!\nBedenke, dass du den Server neustarten musst damit das Team erkannt wird!");
	//			return true;
	//		}
	//		return false;
	//	}
}

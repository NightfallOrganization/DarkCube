/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.darkessentials.util.ExpFix;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;

public class CommandXp extends EssentialsCommand {

	public CommandXp() {
		super("experience", new String[] {"xp"}, b -> b.then(Commands.literal("add")
						.then(Commands.argument("targets", EntityArgument.players())
								.then(Commands.argument("amount", IntegerArgumentType.integer(0))
										.executes(ctx -> add(ctx, false))
										.then(Commands.literal("levels").executes(ctx -> add(ctx, true)))
										.then(Commands.literal("points")
												.executes(ctx -> add(ctx, false))))))
				.then(Commands.literal("set")
						.then(Commands.argument("targets", EntityArgument.players())
								.then(Commands.argument("amount", IntegerArgumentType.integer(0))
										.executes(ctx -> set(ctx, false))
										.then(Commands.literal("levels")
												.executes(ctx -> set(ctx, true)))
										.then(Commands.literal("points")
												.executes(ctx -> set(ctx, false))))))
				.then(Commands.literal("remove")
						.then(Commands.argument("targets", EntityArgument.players())
								.then(Commands.argument("amount", IntegerArgumentType.integer(0))
										.executes(ctx -> remove(ctx, false))
										.then(Commands.literal("levels")
												.executes(ctx -> remove(ctx, true)))
										.then(Commands.literal("points")
												.executes(ctx -> remove(ctx, false))))))
				.then(Commands.literal("get")
						.then(Commands.argument("targets", EntityArgument.players())
								.executes(CommandXp::get))));
	}

	private static int add(CommandContext<CommandSource> context, boolean levels)
			throws CommandSyntaxException {
		Collection<Player> targets = EntityArgument.getPlayers(context, "targets");
		int raw = IntegerArgumentType.getInteger(context, "amount");

		for (Player target : targets) {
			if (levels) {
				target.giveExpLevels(raw);
			} else
				target.giveExp(raw);
		}
		if (targets.size() == 1) {
			context.getSource().sendMessage(
					Message.XP_ADDED_TO_PLAYER.getMessage(context.getSource().getSource(),
							Objects.requireNonNull(targets.stream().findAny().orElse(null))
									.getDisplayName(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		} else {
			context.getSource().sendMessage(
					Message.XP_ADDED_TO_PLAYERS.getMessage(context.getSource().getSource(),
							targets.size(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		}
		return 0;
	}

	private static int set(CommandContext<CommandSource> context, boolean levels)
			throws CommandSyntaxException {
		Collection<Player> targets = EntityArgument.getPlayers(context, "targets");
		int raw = IntegerArgumentType.getInteger(context, "amount");
		for (Player target : targets) {
			target.setExp(0);
			target.setLevel(0);
			target.setTotalExperience(0);
			if (levels) {
				target.giveExpLevels(raw);
			} else {
				target.giveExp(raw);
			}
		}
		if (targets.size() == 1) {
			context.getSource().sendMessage(
					Message.XP_OF_PLAYER_SET.getMessage(context.getSource().getSource(),
							Objects.requireNonNull(targets.stream().findAny().orElse(null))
									.getDisplayName(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		} else {
			context.getSource().sendMessage(
					Message.XP_OF_PLAYERS_SET.getMessage(context.getSource().getSource(),
							targets.size(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		}
		return 0;
	}

	private static int remove(CommandContext<CommandSource> context, boolean levels)
			throws CommandSyntaxException {
		Collection<Player> targets = EntityArgument.getPlayers(context, "targets");
		int raw = IntegerArgumentType.getInteger(context, "amount");

		for (Player target : targets) {
			if (levels) {
				if (target.getLevel() - raw <= 0) {
					target.setLevel(0);
					target.setTotalExperience(0);
					target.setExp(0);
				} else {
					target.setLevel(target.getLevel() - raw);
				}
			} else
				ExpFix.setTotalExperience(target, ExpFix.getTotalExperience(target) - raw);
		}
		if (targets.size() == 1) {
			context.getSource().sendMessage(
					Message.XP_REMOVED_FROM_PLAYER.getMessage(context.getSource().getSource(),
							Objects.requireNonNull(targets.stream().findAny().orElse(null))
									.getDisplayName(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		} else {
			context.getSource().sendMessage(
					Message.XP_REMOVED_FROM_PLAYERS.getMessage(context.getSource().getSource(),
							targets.size(), raw,
							(levels ? Message.XP_LEVELS : Message.XP_POINTS).getMessageString(
									context.getSource().getSource())));
		}
		return 0;
	}

	private static int get(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Component ccb = Component.text("");
		ccb = ccb.appendNewline();
		Collection<Player> targets = EntityArgument.getPlayers(context, "targets");
		for (Player target : targets) {
			int points = ExpFix.getTotalExperience(target);
			int levels = target.getLevel();
			ccb = ccb.append(Message.XP_OF_PLAYER.getMessage(context.getSource().getSource(),
					target.getDisplayName(), levels, points)).appendNewline();
		}
		context.getSource().sendMessage(ccb);
		return 0;
	}

	//	public boolean execute(CommandSender sender, String[] args) {
	//		int count = 0;
	//		int value = 0;
	//		boolean modifyLevels = false;
	//		String action = "set";
	//		Set<Player> players = new HashSet<>();
	//		Set<String> unresolvedNames = new HashSet<>();
	//		if (args.length == 1 && sender instanceof Player) {
	//			if (args[0].equalsIgnoreCase("get")) {
	//				DarkEssentials.getInstance().sendMessage(
	//						DarkEssentials.cConfirm() + "Du hast aktuell " + DarkEssentials.cValue()
	//								+ ExpFix.getTotalExperience((Player) sender)
	//								+ DarkEssentials.cConfirm() + " Erfahrungspunkte. ("
	//								+ DarkEssentials.cValue() + ((Player) sender).getLevel()
	//								+ DarkEssentials.cConfirm() + " Level", sender);
	//				return true;
	//			}
	//			return false;
	//		} else if (args.length > 1) {
	//			try {
	//				switch (args[0].toLowerCase()) {
	//					case "get":
	//						args[0] = "%processed%";
	//						for (String playerName : args) {
	//							if (!playerName.equals("%processed%")) {
	//								if (Bukkit.getPlayer(playerName) != null) {
	//									Player current = Bukkit.getPlayer(playerName);
	//									StringBuilder stringBuilder = new StringBuilder();
	//									stringBuilder.append("§aDer Spieler §5")
	//											.append(current.getName()).append(" §ahat aktuell §5")
	//											.append(ExpFix.getTotalExperience(current))
	//											.append(" §aErfahrungspunkte (§5")
	//											.append(current.getLevel()).append(" §aLevel)");
	//									DarkEssentials.getInstance()
	//											.sendMessage(stringBuilder.toString(), sender);
	//								} else {
	//									unresolvedNames.add(playerName);
	//								}
	//							}
	//						}
	//						DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
	//						return true;
	//					case "set":
	//					case "add":
	//					case "subtract":
	//						if (args[1].charAt(args[1].length() - 1) == 'l'
	//								|| args[1].charAt(args[1].length() - 1) == 'L') {
	//							try {
	//								value = Integer.parseInt(
	//										args[1].substring(0, args[1].length() - 1));
	//								args[1] = "%processed%";
	//								modifyLevels = true;
	//							} catch (Exception e) {
	//								DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail()
	//												+ "Du musst eine Zahl oder eine Zalhl+L §cangeben!",
	//										sender);
	//								return true;
	//							}
	//						} else {
	//							try {
	//								value = Integer.parseInt(args[1]);
	//								args[1] = "%processed%";
	//							} catch (Exception e) {
	//								DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail()
	//												+ "Du musst eine Zahl oder eine Zalhl+L §cangeben!",
	//										sender);
	//								return true;
	//							}
	//						}
	//						action = args[0];
	//						args[0] = "%processed%";
	//						for (String playerName : args) {
	//							if (!playerName.equals("%processed%")) {
	//								if (Bukkit.getPlayer(playerName) != null) {
	//									players.add(Bukkit.getPlayer(playerName));
	//								} else {
	//									unresolvedNames.add(playerName);
	//								}
	//							}
	//						}
	//						break;
	//					default:
	//						throw new IllegalArgumentException();
	//				}
	//			} catch (Exception e) {
	//				DarkEssentials.getInstance()
	//						.sendMessage(DarkEssentials.cFail() + "Du musst eine Aktion angeben!",
	//								sender);
	//				return true;
	//			}
	//
	//		} else {
	//			return false;
	//		}
	//		if (players.isEmpty() && unresolvedNames.isEmpty()) {
	//			if (sender instanceof Player) {
	//				players.add((Player) sender);
	//			} else {
	//				DarkEssentials.sendMessagePlayernameRequired(sender);
	//				return true;
	//			}
	//
	//		}
	//		DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
	//		if (action.equalsIgnoreCase("set")) {
	//			for (Player current : players) {
	//				if (modifyLevels) {
	//					current.setLevel(value);
	//					DarkEssentials.getInstance().sendMessage(
	//							DarkEssentials.cConfirm() + "Deine Level wurden auf "
	//									+ DarkEssentials.cValue() + value + DarkEssentials.cConfirm()
	//									+ " gesetzt.", current);
	//				} else {
	//					ExpFix.setTotalExperience(current, value);
	//					DarkEssentials.getInstance().sendMessage(
	//							DarkEssentials.cConfirm() + "Deine Erfahrungspunkte wurden auf "
	//									+ DarkEssentials.cValue() + value + DarkEssentials.cConfirm()
	//									+ " gesetzt.", current);
	//				}
	//				count++;
	//			}
	//		} else {
	//			for (Player current : players) {
	//				if (modifyLevels) {
	//					if (action.equalsIgnoreCase("add")) {
	//						current.setLevel(current.getLevel() + value);
	//						DarkEssentials.getInstance().sendMessage(
	//								DarkEssentials.cConfirm() + "Du hast " + DarkEssentials.cValue()
	//										+ value + DarkEssentials.cConfirm() + " Level erhalten.",
	//								current);
	//					} else {
	//						current.setLevel(current.getLevel() - value);
	//						DarkEssentials.getInstance().sendMessage(
	//								DarkEssentials.cConfirm() + "Du hast " + DarkEssentials.cValue()
	//										+ value + DarkEssentials.cConfirm() + " Level verloren.",
	//								current);
	//					}
	//				} else {
	//					if (action.equalsIgnoreCase("add")) {
	//						ExpFix.setTotalExperience(current,
	//								ExpFix.getTotalExperience(current) + value);
	//						DarkEssentials.getInstance().sendMessage(
	//								DarkEssentials.cConfirm() + "Du hast " + DarkEssentials.cValue()
	//										+ value + DarkEssentials.cConfirm()
	//										+ " Erfahrungspunkte erhalten.", current);
	//					} else {
	//						try {
	//							ExpFix.setTotalExperience(current,
	//									ExpFix.getTotalExperience(current) - value);
	//						} catch (Exception e) {
	//							ExpFix.setTotalExperience(current, 0);
	//						}
	//						DarkEssentials.getInstance().sendMessage(
	//								DarkEssentials.cConfirm() + "Du hast " + DarkEssentials.cValue()
	//										+ value + DarkEssentials.cConfirm()
	//										+ " Erfahrungspunkte verloren.", current);
	//
	//					}
	//
	//				}
	//				count++;
	//			}
	//		}
	//		if (!(players.size() == 1 && players.contains(sender)))
	//			DarkEssentials.getInstance().sendMessage(
	//					DarkEssentials.cConfirm() + "Erfahrungspunkte von " + DarkEssentials.cValue()
	//							+ count + DarkEssentials.cConfirm() + " Spielern verändert!", sender);
	//		return true;
	//	}
	//
	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		if (args.length == 1) {
	//			return EssentialCollections.toSortedStringList(
	//					EssentialCollections.asList("get", "set", "add", "subtract"), args[0]);
	//		}
	//		if (args.length > 2 || (args.length == 2 && args[0].equalsIgnoreCase("get"))) {
	//			return DarkEssentials.getPlayersStartWith(args);
	//		}
	//		return new ArrayList<>();
	//	}
}

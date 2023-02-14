/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.troll;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.PerkArgument;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.EntitySelector;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CommandSetPerk extends WBCommandExecutor {
	public CommandSetPerk() {
		super("setPerk", b -> {
			RequiredArgumentBuilder<CommandSource, EntitySelector> b2 =
					Commands.argument("players", EntityArgument.players());
			for (ActivationType type : ActivationType.values()) {
				b2.then(Commands.literal(type.name().toLowerCase())
						.then(Commands.argument("perkSlot",
										IntegerArgumentType.integer(1, type.maxCount()))
								.then(Commands.argument("perk", PerkArgument.singlePerkArgument())
										.executes(ctx -> {
											int perkSlot =
													IntegerArgumentType.getInteger(ctx, "perkSlot");
											Perk perk = PerkArgument.getPerk(ctx, "perk");
											Collection<Player> targetPlayers =
													EntityArgument.getPlayers(ctx, "players");
											for (Player player : targetPlayers) {
												WBUser user = WBUser.getUser(player);
												PlayerPerks perks = user.perksStorage();
												perks.perk(type, perkSlot, perk.perkName());
												user.perksStorage(perks);
												user.perks().reloadFromStorage();
												ctx.getSource()
														.sendMessage(Message.PERK_SET_FOR_PLAYER,
																type.name().toLowerCase(), perkSlot,
																user.getTeamPlayerName(),
																perk.perkName().getName()
																		.toLowerCase());
											}
											return 0;
										}))));
			}
			b.then(b2);

		});
	}
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		WBUser target = null;
	//		int i = 0;
	//		if (args.length == 2) {
	//			if (sender instanceof Player) {
	//				target = WoolBattle.getInstance().getUserWrapper()
	//						.getUser(((Player) sender).getUniqueId());
	//			}
	//		} else if (args.length == 3) {
	//			i = 1;
	//			Player p = Bukkit.getPlayer(args[0]);
	//			if (p != null) {
	//				target = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
	//			}
	//		}
	//		if (target == null) {
	//			return false;
	//		}
	//		PerkNumber number = number(args[i]);
	//		if (number == null) {
	//			WoolBattle.getInstance().sendMessage("§cInvalid PerkSlot", sender);
	//			return true;
	//		}
	//		PerkType perk = null;
	//		PerkType[] a = getPerks(number);
	//		if (a != null) {
	//			for (PerkType t : a) {
	//				if (t.getPerkName().getName().equals(args[i + 1])) {
	//					perk = t;
	//					break;
	//				}
	//			}
	//			if (perk == null) {
	//				WoolBattle.getInstance().sendMessage("§cInvalid Perk", sender);
	//				return true;
	//			}
	//		}
	//		UserPerkOld rperk =
	//				perk != null ? perk.newPerkTypePerk(target, number) : new PerkEnderPearl(target);
	//		switch (number) {
	//			case ACTIVE_1:
	//				target.setActivePerk1(rperk);
	//				break;
	//			case ACTIVE_2:
	//				target.setActivePerk2(rperk);
	//				break;
	//			case PASSIVE:
	//				target.setPassivePerk(rperk);
	//				break;
	//			case ENDER_PEARL:
	//				target.setEnderPearl(rperk);
	//				break;
	//			default:
	//				WoolBattle.getInstance().sendMessage("§cError: " + number, sender);
	//				return true;
	//		}
	//		WoolBattle.getInstance().getIngame().setPlayerItems(target);
	//		WoolBattle.getInstance()
	//				.sendMessage("§aPerk set: " + number + " | " + rperk.getPerkName().getName(),
	//						sender);
	//		return true;
	//	}
	//
	//	private PerkType[] getPerks(PerkNumber number) {
	//		switch (number) {
	//			case ACTIVE_1:
	//			case ACTIVE_2:
	//				return Arrays.asList(PerkType.values()).stream().filter(p -> !p.isPassive())
	//						.collect(Collectors.toList()).toArray(new PerkType[0]);
	//			case PASSIVE:
	//				return Arrays.asList(PerkType.values()).stream().filter(p -> p.isPassive())
	//						.collect(Collectors.toList()).toArray(new PerkType[0]);
	//			case ENDER_PEARL:
	//				return null;
	//			default:
	//				break;
	//		}
	//		return new PerkType[0];
	//	}
	//
	//	private PerkNumber number(String s) {
	//		for (PerkNumber number : PerkNumber.values()) {
	//			if (number != PerkNumber.DISPLAY) {
	//				if (s.equalsIgnoreCase(number.toString())) {
	//					return number;
	//				}
	//			}
	//		}
	//		return null;
	//	}
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.command.argument.PerkArgument;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

import java.util.Collection;

public class TrollCommand extends PServerExecutor {

	public TrollCommand() {
		super("troll", new String[0], b -> b.then(Commands.literal("setPerkCooldown")
				.then(Commands.argument("perk", PerkArgument.perkArgument())
						.then(Commands.argument("cooldown", IntegerArgumentType.integer(0, 64))
								.executes(context -> {
									Collection<Perk> perks =
											PerkArgument.getPerkTypes(context, "perk");
									CommandSource source = context.getSource();
									int cooldown =
											IntegerArgumentType.getInteger(context, "cooldown");
									for (Perk perk : perks) {
										perk.cooldown(cooldown);
									}
									if (perks.size() == 1) {
										source.sendMessage(
												Message.WOOLBATTLE_TROLL_SETPERKCOOLDOWN_SINGLE,
												perks.stream().findAny().get().perkName().getName(),
												cooldown);
									} else {
										source.sendMessage(
												Message.WOOLBATTLE_TROLL_SETPERKCOOLDOWN_MULTIPLE,
												perks.size(), cooldown);
									}
									return 0;
								})))).then(Commands.literal("setPerkCost")
				.then(Commands.argument("perk", PerkArgument.perkArgument())
						.then(Commands.argument("cost", IntegerArgumentType.integer(0, 99))
								.executes(context -> {
									Collection<Perk> perks =
											PerkArgument.getPerkTypes(context, "perk");
									CommandSource source = context.getSource();
									int cooldown = IntegerArgumentType.getInteger(context, "cost");
									for (Perk perk : perks) {
										perk.cost(cooldown);
									}
									if (perks.size() == 1) {
										source.sendMessage(
												Message.WOOLBATTLE_TROLL_SETPERKCOST_SINGLE,
												perks.stream().findAny().get().perkName().getName(),
												cooldown);
									} else {
										source.sendMessage(
												Message.WOOLBATTLE_TROLL_SETPERKCOST_MULTIPLE,
												perks.size(), cooldown);
									}
									return 0;
								})))).then(Commands.literal("resetPerkCooldown")
				.then(Commands.argument("perk", PerkArgument.perkArgument()).executes(context -> {
					Collection<Perk> perks = PerkArgument.getPerkTypes(context, "perk");
					CommandSource source = context.getSource();
					for (Perk perk : perks) {
						perk.cooldown(perk.defaultCooldown());
					}
					if (perks.size() == 1) {
						source.sendMessage(Message.WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_SINGLE,
								perks.stream().findAny().get().perkName().getName());
					} else {
						source.sendMessage(Message.WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_MULTIPLE,
								perks.size());
					}
					return 0;
				}))).then(Commands.literal("resetPerkCost")
				.then(Commands.argument("perk", PerkArgument.perkArgument()).executes(context -> {
					Collection<Perk> perks = PerkArgument.getPerkTypes(context, "perk");
					CommandSource source = context.getSource();
					for (Perk perk : perks) {
						perk.cost(perk.defaultCost());
					}
					if (perks.size() == 1) {
						source.sendMessage(Message.WOOLBATTLE_TROLL_RESETPERKCOST_SINGLE,
								perks.stream().findAny().get().perkName().getName());
					} else {
						source.sendMessage(Message.WOOLBATTLE_TROLL_RESETPERKCOST_MULTIPLE,
								perks.size());
					}
					return 0;
				}))));
	}

}

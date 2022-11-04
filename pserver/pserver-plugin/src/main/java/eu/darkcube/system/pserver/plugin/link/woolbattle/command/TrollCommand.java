package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import java.util.Collection;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.minigame.woolbattle.command.argument.PerkArgument;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class TrollCommand extends PServerExecutor {

	public TrollCommand() {
		super("troll", new String[0],
						b -> b.then(Commands.literal("setPerkCooldown").then(Commands.argument("perk", PerkArgument.perkArgument()).then(Commands.argument("cooldown", IntegerArgumentType.integer(0, 64)).executes(context -> {
							Collection<PerkType> perks = PerkArgument.getPerkTypes(context, "perk");
							CommandSource source = context.getSource();
							int cooldown = IntegerArgumentType.getInteger(context, "cooldown");
							for (PerkType perk : perks) {
								perk.setCooldown(cooldown);
							}
							if (perks.size() == 1) {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_SETPERKCOOLDOWN_SINGLE.getMessage(source, perks.stream().findAny().get().getPerkName().getName(), cooldown), true);
							} else {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_SETPERKCOOLDOWN_MULTIPLE.getMessage(source, perks.size(), cooldown), true);
							}
							return 0;
						})))).then(Commands.literal("setPerkCost").then(Commands.argument("perk", PerkArgument.perkArgument()).then(Commands.argument("cost", IntegerArgumentType.integer(0, 99)).executes(context -> {
							Collection<PerkType> perks = PerkArgument.getPerkTypes(context, "perk");
							CommandSource source = context.getSource();
							int cooldown = IntegerArgumentType.getInteger(context, "cost");
							for (PerkType perk : perks) {
								perk.setCost(cooldown);
							}
							if (perks.size() == 1) {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_SETPERKCOST_SINGLE.getMessage(source, perks.stream().findAny().get().getPerkName().getName(), cooldown), true);
							} else {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_SETPERKCOST_MULTIPLE.getMessage(source, perks.size(), cooldown), true);
							}
							return 0;
						})))).then(Commands.literal("resetPerkCooldown").then(Commands.argument("perk", PerkArgument.perkArgument()).executes(context -> {
							Collection<PerkType> perks = PerkArgument.getPerkTypes(context, "perk");
							CommandSource source = context.getSource();
							for (PerkType perk : perks) {
								perk.setCooldown(perk.getDefaultCooldown());
							}
							if (perks.size() == 1) {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_SINGLE.getMessage(source, perks.stream().findAny().get().getPerkName().getName()), true);
							} else {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_RESETPERKCOOLDOWN_MULTIPLE.getMessage(source, perks.size()), true);
							}
							return 0;
						}))).then(Commands.literal("resetPerkCost").then(Commands.argument("perk", PerkArgument.perkArgument()).executes(context -> {
							Collection<PerkType> perks = PerkArgument.getPerkTypes(context, "perk");
							CommandSource source = context.getSource();
							for (PerkType perk : perks) {
								perk.setCost(perk.getDefaultCost());
							}
							if (perks.size() == 1) {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_RESETPERKCOST_SINGLE.getMessage(source, perks.stream().findAny().get().getPerkName().getName()), true);
							} else {
								source.sendFeedback(Message.WOOLBATTLE_TROLL_RESETPERKCOST_MULTIPLE.getMessage(source, perks.size()), true);
							}
							return 0;
						}))));
	}

}
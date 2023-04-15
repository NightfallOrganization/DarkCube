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
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;

import java.util.Collection;

public class CommandSetPerkCooldown extends WBCommandExecutor {
	public CommandSetPerkCooldown() {
		super("setPerkCooldown", b -> b.then(Commands.argument("perks", PerkArgument.perkArgument())
				.then(Commands.argument("cooldown", IntegerArgumentType.integer(0, 64))
						.executes(ctx -> {
							Collection<Perk> perks = PerkArgument.getPerkTypes(ctx, "perks");
							int cooldown = IntegerArgumentType.getInteger(ctx, "cooldown");
							for (Perk perk : perks) {
								perk.cooldown(new Perk.Cooldown(perk.cooldown().unit(), cooldown));
								ctx.getSource().sendMessage(Message.PERK_COOLDOWN_SET,
										perk.perkName().getName().toLowerCase(), cooldown);
							}
							return 0;
						}))));
	}
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 2) {
	//			String sperk = args[0];
	//			String scd = args[1];
	//
	//			boolean ep = false;
	//			Collection<PerkType> perks = new HashSet<>();
	//			for (PerkType p : PerkType.values()) {
	//				if (p.getPerkName().getName().equalsIgnoreCase(sperk) || sperk.equals("*")) {
	//					perks.add(p);
	//				}
	//			}
	//			if (sperk.equals("ENDERPEARL") || sperk.equals("*")) {
	//				ep = true;
	//			}
	//			int cd;
	//			try {
	//				cd = Integer.parseInt(scd);
	//				if (cd < 0) {
	//					cd = 0;
	//				}
	//			} catch (Exception ex) {
	//				WoolBattle.getInstance().sendMessage("§cUngültige Zahl: " + scd, sender);
	//				return true;
	//			}
	//			final int fcd = cd;
	//			for (PerkType type : perks) {
	//				//				type.setCooldown(cd);
	//				//				Main.getInstance().sendMessage("§aDu hast den Cooldown für das Perk §6" + type.getPerkName().getName()
	//				//						+ " §aauf §6" + cd + "§a gesetzt!");
	//				setCD(() -> type.setCooldown(fcd), fcd, type.getPerkName().getName(), sender);
	//			}
	//			if (ep) {
	//				setCD(() -> PerkEnderPearl.COOLDOWN = fcd, fcd, "ENDERPEARL", sender);
	//			}
	//			return true;
	//		}
	//		return false;
	//	}
	//
	//	private void setCD(Runnable r, int cd, String name, CommandSender sender) {
	//		r.run();
	//		WoolBattle.getInstance().sendMessage(
	//				"§aDu hast den Cooldown für das Perk §6" + name + " §aauf §6" + cd + "§a gesetzt!",
	//				sender);
	//	}
}

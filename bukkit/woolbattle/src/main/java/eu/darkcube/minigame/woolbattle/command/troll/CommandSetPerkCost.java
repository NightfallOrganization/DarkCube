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

public class CommandSetPerkCost extends WBCommandExecutor {
	public CommandSetPerkCost() {
		super("setPerkCost", b -> b.then(Commands.argument("perks", PerkArgument.perkArgument())
				.then(Commands.argument("cost", IntegerArgumentType.integer(0)).executes(ctx -> {
					Collection<Perk> perks = PerkArgument.getPerkTypes(ctx, "perks");
					int cost = IntegerArgumentType.getInteger(ctx, "cost");
					for (Perk perk : perks) {
						perk.cost(cost);
						ctx.getSource().sendMessage(Message.PERK_COST_SET,
								perk.perkName().getName().toLowerCase(), cost);
					}
					return 0;
				}))));
	}
	//	private static String[] perks;
	//
	//	static {
	//		List<String> p = new ArrayList<>();
	//		p.add("*");
	//		p.add("ENDERPEARL");
	//		for (PerkType type : PerkType.values()) {
	//			p.add(type.getPerkName().getName());
	//		}
	//		perks = p.toArray(new String[0]);
	//	}
	//
	//	public CommandSetPerkCost() {
	//		super(WoolBattle.getInstance(), "setPerkCost", new Command[0], "Setzt die Perk Kosten",
	//				CommandArgument.PERK, CommandArgument.COST);
	//	}
	//
	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		if (args.length == 1) {
	//			return Arrays.toSortedStringList(perks, args[0]);
	//		} else if (args.length == 2) {
	//			return Arrays.toSortedStringList(
	//					new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
	//							19, 20}, args[1]);
	//		}
	//		return super.onTabComplete(args);
	//	}
	//
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
	//				setCD(() -> type.setCost(fcd), fcd, type.getPerkName().getName(), sender);
	//			}
	//			if (ep) {
	//				setCD(() -> PerkEnderPearl.COST = fcd, fcd, "ENDERPEARL", sender);
	//			}
	//			return true;
	//		}
	//		return false;
	//	}
	//
	//	private void setCD(Runnable r, int cd, String name, CommandSender sender) {
	//		r.run();
	//		WoolBattle.getInstance().sendMessage(
	//				"§aDu hast die Kosten für das Perk §6" + name + " §aauf §6" + cd + "§a gesetzt!",
	//				sender);
	//	}
}

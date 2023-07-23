/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

public class CommandDisable extends WBCommandExecutor {
	public CommandDisable() {
		super("disable", b -> b.executes(ctx -> {
			TeamType team = TeamArgument.getTeam(ctx, "team");
			if (!team.isEnabled()) {
				ctx.getSource().sendMessage(Component.text("Dieses Team ist bereits deaktiviert!")
						.color(NamedTextColor.RED));
				return 0;
			}
			team.setEnabled(false);
			ctx.getSource().sendMessage(Component.text(
							"Du hast das Team '" + team.getDisplayNameKey() + "' deaktiviert!")
					.color(NamedTextColor.GRAY));
			return 0;
		}));
	}
	//	public CommandDisable() {
	//		super(WoolBattle.getInstance(), "disable", new Command[0], "Deaktiviert das Team");
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 0) {
	//			TeamType team = TeamType.byDisplayNameKey(getSpaced());
	//			if (team == null || team.isDeleted()) {
	//				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
	//				return true;
	//			}
	//			if (!team.isEnabled()) {
	//				sender.sendMessage("§cDieses Team ist bereits deaktiviert!");
	//				return true;
	//			}
	//			team.setEnabled(false);
	//			sender.sendMessage("§7Du hast das Team '" + team.getDisplayNameKey() + "' deaktiviert!");
	//			return true;
	//		}
	//		return false;
	//	}
}

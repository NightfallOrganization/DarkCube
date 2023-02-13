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
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class CommandInfo extends WBCommandExecutor {
	public CommandInfo() {
		super("info", b -> b.executes(ctx -> {
			TeamType team = TeamArgument.getTeam(ctx, "team");
			@SuppressWarnings("deprecation")
			String sb =
					"§aTeam: " + team.getDisplayNameKey() + "\nNamensfarbe: §" + team.getNameColor()
							+ ChatColor.getByChar(team.getNameColor()).name() + "\n§aWollfarbe: "
							+ DyeColor.getByData(team.getWoolColorByte()) + "\nSortierung: "
							+ team.getWeight() + "\nMaximale Spieleranzahl: " + team.getMaxPlayers()
							+ "\nAktiviert: " + team.isEnabled();
			ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize(sb));
			return 0;
		}));
	}
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 0) {
	//			TeamType team = TeamType.byDisplayNameKey(getSpaced());
	//			if (team == null || team.isDeleted()) {
	//				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
	//				return true;
	//			}
	//			StringBuilder b = new StringBuilder();
	//			b.append("§aTeam: " + team.getDisplayNameKey()).append("\nNamensfarbe: §").append(team.getNameColor())
	//					.append(ChatColor.getByChar(team.getNameColor()).name()).append("\n§aWollfarbe: ")
	//					.append(DyeColor.getByData(team.getWoolColorByte())).append("\nSortierung: ").append(team.getWeight())
	//					.append("\nMaximale Spieleranzahl: ").append(team.getMaxPlayers()).append("\nAktiviert: ")
	//					.append(team.isEnabled());
	//			sender.sendMessage(b.toString());
	//			return true;
	//		}
	//		return false;
	//	}

}

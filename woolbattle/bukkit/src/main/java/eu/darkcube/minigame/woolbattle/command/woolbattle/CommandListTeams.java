/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandListTeams extends WBCommandExecutor {
    public CommandListTeams(WoolBattleBukkit woolbattle) {
        super("listTeams", b -> b.executes(ctx -> {
            Component c = Component.empty();
            TeamType[] types = woolbattle.teamManager().teamTypes().stream().filter(tt -> woolbattle.teamManager().getSpectator().getType() != tt).toArray(TeamType[]::new);
            if (types.length == 0) {
                c = Component.text("Es sind keine Teams erstellt!");
            } else {
                for (TeamType type : types) {
                    c = c.append(Component.text(" - " + type.getDisplayNameKey()).append(Component.newline()));
                }
            }
            ctx.getSource().sendMessage(c);
            return 0;
        }));
    }
    //	public CommandListTeams() {
    //		super(WoolBattle.getInstance(), "listTeams", new Command[0], "Listet alle Teams auf");
    //	}
    //
    //	@SuppressWarnings("deprecation")
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		TeamType[] types = TeamType.values();
    //		if (types.length == 0) {
    //			sender.sendMessage("§cEs sind keine Teams erstellt!");
    //			return true;
    //		}
    //		StringBuilder b = new StringBuilder();
    //		for (int i = 0; i < types.length; i++) {
    //			TeamType t = types[i];
    //			b.append("§7- Name: '§5").append(t.getDisplayNameKey()).append("§7', Sortierung: §5").append(t.getWeight())
    //					.append("§7, Max. Spieler: §5").append(t.getMaxPlayers()).append("§7, Wollfarbe: §5")
    //					.append(DyeColor.getByData(t.getWoolColorByte())).append("§7, Namenfarbe: §5")
    //					.append(ChatColor.getByChar(t.getNameColor()).name()).append('\n');
    //		}
    //		sender.sendMessage(b.toString());
    //		return true;
    //	}
}

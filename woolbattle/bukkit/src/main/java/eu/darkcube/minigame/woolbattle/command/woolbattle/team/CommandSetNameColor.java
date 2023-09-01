/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EnumArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class CommandSetNameColor extends WBCommand {
    public CommandSetNameColor() {
        super("setNameColor", b -> b.then(Commands
                .argument("nameColor", EnumArgument.enumArgument(Arrays
                        .stream(ChatColor.values())
                        .filter(ChatColor::isColor)
                        .toArray(ChatColor[]::new), c -> new String[]{c.name().toLowerCase()}))
                .executes(ctx -> {
                    TeamType team = TeamTypeArgument.teamType(ctx, "team");
                    ChatColor nameColor = EnumArgument.getEnumArgument(ctx, "nameColor", ChatColor.class);
                    team.setNameColor(nameColor);
                    ctx
                            .getSource()
                            .sendMessage(LegacyComponentSerializer
                                    .legacySection()
                                    .deserialize("§7Du hast die Namensfarbe des Teams " + team.getDisplayNameKey() + " zu " + nameColor + nameColor.name() + "§7 geändert."));
                    return 0;
                })));
    }
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 1) {
    //			TeamType team = TeamType.byDisplayNameKey(getSpaced());
    //			if (team == null || team.isDeleted()) {
    //				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
    //				return true;
    //			}
    //			ChatColor chatColor = ChatColor.getByChar(ColorUtil.byChatColor(args[0]));
    //			if (chatColor == null) {
    //				sender.sendMessage("§cBitte gib eine gültige Namenfarbe an!");
    //				return true;
    //			}
    //			team.setNameColor(chatColor);
    //			sender.sendMessage("§7Du hast die Namensfarbe des Teams " + team.getDisplayNameKey() + " zu " + chatColor
    //					+ chatColor.name() + "§7 geändert.");
    //			return true;
    //		}
    //		return false;
    //	}
}

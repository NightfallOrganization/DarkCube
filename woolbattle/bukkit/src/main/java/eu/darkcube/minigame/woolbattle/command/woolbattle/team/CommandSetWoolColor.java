/*
 * Copyright (c) 2022-2024. [DarkCube]
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
import org.bukkit.DyeColor;

public class CommandSetWoolColor extends WBCommand {
    public CommandSetWoolColor() {
        super("setWoolColor", b -> b.then(Commands.argument("woolColor", EnumArgument.enumArgument(DyeColor.values(), c -> new String[]{c.name().toLowerCase()})).executes(ctx -> {
            TeamType team = TeamTypeArgument.teamType(ctx, "team");
            DyeColor woolColor = EnumArgument.getEnumArgument(ctx, "woolColor", DyeColor.class);
            team.setWoolColor(woolColor);
            ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize("§7Du hast die Wollfarbe des Teams " + team.getDisplayNameKey() + " zu " + woolColor.name() + "§7 geändert."));
            return 0;
        })));
    }
}

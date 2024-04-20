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
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

public class CommandEnable extends WBCommand {
    public CommandEnable() {
        super("enable", b -> b.executes(ctx -> {
            TeamType team = TeamTypeArgument.teamType(ctx, "team");
            if (team.isEnabled()) {
                ctx.getSource().sendMessage(Component.text("Dieses Team ist bereits aktiviert!").color(NamedTextColor.RED));
                return 0;
            }
            team.setEnabled(true);
            ctx.getSource().sendMessage(Component.text("Du hast das Team '" + team.getDisplayNameKey() + "' aktiviert!").color(NamedTextColor.GRAY));
            return 0;
        }));
    }
}

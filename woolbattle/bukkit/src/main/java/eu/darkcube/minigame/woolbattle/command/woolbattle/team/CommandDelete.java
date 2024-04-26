/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandDelete extends WBCommand {
    public CommandDelete() {
        super("delete", b -> b.executes(ctx -> {
            TeamType teamType = TeamTypeArgument.teamType(ctx, "team");
            teamType.delete();
            ctx.getSource().sendMessage(Component.text("Team " + teamType.getDisplayNameKey() + " gelöscht!"));
            return 0;
        }));
    }
}

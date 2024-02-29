/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.command;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.argument.BooleanArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import org.bukkit.entity.Player;

public class CommandTeam extends Command {

    public CommandTeam() {
        super("miners", "team", new String[0], b -> b.then(Commands
                .argument("team", IntegerArgumentType.integer(0, Miners.getMinersConfig().TEAM_COUNT))
                .executes(context -> setTeam(context.getSource().asPlayer(), IntegerArgumentType.getInteger(context, "team"), false))
                .then(Commands
                        .argument("allowInFullTeam", BooleanArgument.booleanArgument())
                        .executes(context -> setTeam(context
                                .getSource()
                                .asPlayer(), IntegerArgumentType.getInteger(context, "team"), BooleanArgument.getBoolean(context, "allowInFullTeam"))))));
    }

    private static int setTeam(Player player, int team, boolean allowInFullTeam) {
        if (Miners.getGamephase() != 0) {
            Miners.sendTranslatedMessage(player, Message.FAIL_GAME_RUNNING);
            return 0;
        }
        if (!Miners.getTeamManager().setPlayerTeam(player, team, allowInFullTeam))
            Miners.sendTranslatedMessage(player, Message.FAIL_TEAM_FULL);
        return 0;
    }

}

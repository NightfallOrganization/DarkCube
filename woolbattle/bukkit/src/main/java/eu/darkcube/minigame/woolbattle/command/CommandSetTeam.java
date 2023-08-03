/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CommandSetTeam extends CommandExecutor {

    public CommandSetTeam(WoolBattle woolbattle) {
        super("woolbattle", "setteam", "woolbattle.command.setteam", new String[0], b -> {
            b.then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("team", TeamArgument.teamArgument(woolbattle))
                            .executes(context -> {
                                Player player = EntityArgument.getPlayer(context, "player");
                                WBUser user = WBUser.getUser(player);
                                Team team = TeamArgument.team(context, "team");
                                user.setTeam(team);
                                context.getSource()
                                        .sendMessage(Component.text("Team gesetzt!"));
                                return 0;
                            })));
        });
    }

}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SetTeamCommand extends PServerExecutor {

    public SetTeamCommand(WoolBattleBukkit woolbattle) {
        super("setteam", new String[0], b -> b.then(Commands.argument("team", TeamArgument.teamArgument(woolbattle)).executes(context -> {
            SetTeamCommand.setTeam(woolbattle, context.getSource(), Arrays.asList(context
                    .getSource()
                    .asPlayer()), TeamArgument.team(context, "team"));
            return 0;
        }).then(Commands.argument("players", EntityArgument.players()).executes(context -> {
            SetTeamCommand.setTeam(woolbattle, context.getSource(), EntityArgument.getPlayers(context, "players"), TeamArgument.team(context, "team"));
            return 0;
        }))));
    }

    private static void setTeam(WoolBattleBukkit woolbattle, CommandSource source, Collection<Player> players, Team team) {
        for (Player player : players) {
            WBUser user = WBUser.getUser(player);
            user.setTeam(team);
        }
        if (players.size() == 1) {
            source.sendMessage(Message.WOOLBATTLE_SETTEAM_TEAM_SINGLE, players.stream().findAny().get().getName(), team
                    .getType()
                    .getDisplayNameKey());
        } else {
            source.sendMessage(Message.WOOLBATTLE_SETTEAM_TEAM_MULTIPLE, players.size(), team.getType().getDisplayNameKey());
            //			for (Player player : players) {
            //				User user = Main.getInstance().getUserWrapper().getUser(player.getUniqueId());
            //			}
        }

    }
}

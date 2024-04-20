/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;

public class SetLifesCommand extends PServer {

    public SetLifesCommand(WoolBattleBukkit woolbattle) {
        super("setlifes", new String[0], b -> b.then(Commands.argument("lifes", IntegerArgumentType.integer(0, 999)).requires(source -> woolbattle.lobby().enabled()).executes(context -> {
            int lifes = IntegerArgumentType.getInteger(context, "lifes");
            woolbattle.gameData().forceLifes(lifes);
            context.getSource().sendMessage(Message.WOOLBATTLE_SETLIFES_LIFES, lifes);
            return 0;
        })).then(Commands.argument("team", TeamArgument.teamArgument(woolbattle)).requires(source -> woolbattle.ingame().enabled()).then(Commands.argument("lifes", IntegerArgumentType.integer(0, 999)).executes(context -> {
            TeamType type = TeamTypeArgument.teamType(context, "team");
            int lifes = IntegerArgumentType.getInteger(context, "lifes");
            Team team = woolbattle.teamManager().getTeam(type);
            team.setLifes(lifes);
            context.getSource().sendMessage(Message.WOOLBATTLE_SETLIFES_TEAM_LIFES, Component.text(team.getType().getDisplayNameKey()).style(team.getPrefixStyle()), lifes);
            return 0;
        }))));
    }

}

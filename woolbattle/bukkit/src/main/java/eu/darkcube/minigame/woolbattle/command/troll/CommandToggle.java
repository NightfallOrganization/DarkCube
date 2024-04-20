/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.Collection;
import java.util.Collections;

import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import org.bukkit.entity.Player;

public class CommandToggle extends WBCommand {
    public CommandToggle() {
        super("toggle", b -> b.executes(ctx -> toggle(ctx, Collections.singleton(ctx.getSource().asPlayer()))).then(Commands.argument("players", EntityArgument.players()).executes(ctx -> toggle(ctx, EntityArgument.getPlayers(ctx, "players")))));
    }

    private static int toggle(CommandContext<CommandSource> ctx, Collection<Player> players) {
        for (Player p : players) {
            WBUser user = WBUser.getUser(p);
            user.setTrollMode(!user.isTrollMode());
            StatsLink.enabled = false;
        }
        return 0;
    }
}

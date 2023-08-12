/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandSetSpawn extends WBCommandExecutor {
    public CommandSetSpawn(WoolBattleBukkit woolbattle) {
        super("setSpawn", b -> b.then(Commands
                .argument("map", MapArgument.mapArgument(woolbattle, MapArgument.ToStringFunction.function(ctx -> MapSizeArgument.mapSize(ctx, "mapSize"))))
                .executes(ctx -> set(woolbattle, ctx))));
    }

    private static int set(WoolBattleBukkit woolbattle, CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        TeamType team = TeamTypeArgument.teamType(ctx, "team");
        Map map = MapArgument.getMap(ctx, "map");
        Player player = ctx.getSource().asPlayer();
        if (map.ingameData() == null) {
            player.sendMessage("Map nicht geladen!");
            return 0;
        }
        Location loc = Locations.getNiceLocation(player.getLocation());
        player.teleport(loc);

        map.ingameData().spawn(team.getDisplayNameKey(), loc);

        player.sendMessage("§7Du hast den Spawn für die Map '" + map.getName() + "' neu gesetzt!");
        woolbattle.mapLoader().save(map).thenRun(() -> {
            player.sendMessage("§aErfolgreich gespeichert!");
        });
        return 0;
    }
}

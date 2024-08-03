/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.Vec2Argument;
import eu.darkcube.system.bukkit.commandapi.argument.Vec3Argument;
import eu.darkcube.system.bukkit.commandapi.argument.WorldArgument;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class ImportWorldCommand extends DarkCommand {

    public ImportWorldCommand() {
        //@formatter:off
        super("importworld", new String[]{"iw", "worldimport"}, builder -> {
            builder.then(Commands.argument("world", WorldArgument.world())
                    .executes(context -> executeCommand(context))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context){
        World world = WorldArgument.getWorld(context, "world");
        Bukkit.createWorld(WorldCreator.name(world.getName()));

        return 0;
    }
}
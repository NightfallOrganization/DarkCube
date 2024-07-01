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
import org.bukkit.Location;
import org.bukkit.World;

public class TpWorldCommand extends DarkCommand {

    private static final Vector3d DEFAULT_POSITION = new Vector3d(0.5, 100, 0.5);
    private static final Vector2f DEFAULT_ROTATION = new Vector2f(0, 0);

    public TpWorldCommand() {
        //@formatter:off
        super("tpworld", new String[]{"teleportworld", "worldteleport", "worldtp"}, builder -> {
            builder.then(Commands.argument("world", WorldArgument.world())
                    .executes(context -> executeCommand(context, DEFAULT_POSITION, DEFAULT_ROTATION))

                    .then(Commands.argument("position", Vec3Argument.vec3())
                            .executes(context -> executeCommand(context, Vec3Argument.getVec3(context, "position"), DEFAULT_ROTATION))

                            .then(Commands.argument("rotation", Vec2Argument.vec2())
                                    .executes(context -> executeCommand(context, Vec3Argument.getVec3(context, "position"),Vec2Argument.getVec2f(context, "rotation")))
                            )
                    )
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, Vector3d position, Vector2f rotation) throws CommandSyntaxException {
        World world = WorldArgument.getWorld(context, "world");
        Location location = new Location(world, position.x, position.y, position.z, rotation.x, rotation.y);
        context.getSource().asPlayer().teleport(location);
        context.getSource().sendMessage(Message.TELEPORT, world.getName());

        return 0;
    }
}
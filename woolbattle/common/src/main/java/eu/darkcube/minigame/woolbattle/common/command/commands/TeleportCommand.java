/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.entityArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonVec3Argument;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class TeleportCommand extends WoolBattleCommand {
    public TeleportCommand() {
        super("teleport", new String[]{"tp"}, b -> b.then(argument("target", entityArgument()).executes(ctx -> {
            var source = ctx.getSource();
            var sender = source.sender();
            if (!sender.isPlayer()) return 0;
            var user = (CommonWBUser) sender;

            var target = WoolBattleArguments.entity(ctx, "target");

            user.teleport(target.location());
            return 0;
        })).then(argument("pos", CommonVec3Argument.vec3(true)).executes(ctx -> {
            var source = ctx.getSource();
            var sender = source.sender();
            if (!sender.isPlayer()) return 0;
            var user = (CommonWBUser) sender;

            var target = CommonVec3Argument.getLocation(ctx, "pos");
            var vec = target.getPosition(source);
            var pos = new Position.Directed.Simple(vec.x, vec.y, vec.z, 0, 0);

            user.teleport(pos);
            return 0;
        })));
    }
}

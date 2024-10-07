/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.*;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;

public class ListEntitiesCommand extends WoolBattleCommand {
    public ListEntitiesCommand() {
        super("listentities", b -> b.executes(ctx -> {
            var world = ctx.getSource().world();
            if (world == null) return 0;
            var entities = world.getEntities();
            var msg = empty();
            for (var entity : entities) {
                if (msg != empty()) {
                    msg = msg.append(newline());
                } else {
                    msg = msg.append(text(entities.size()));
                }
                msg = msg.append(text(" - ").append(text(entity.type().key().toString())).append(text(": ")).append(text(entity.location().toString())));
            }
            ctx.getSource().sendMessage(msg);
            return 0;
        }));
    }
}

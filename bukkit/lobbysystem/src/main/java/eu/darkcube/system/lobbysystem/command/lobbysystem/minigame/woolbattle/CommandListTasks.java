/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandListTasks extends LobbyCommand {

    public CommandListTasks() {
        super("listTasks", b -> b.executes(ctx -> {
            var tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
            Component ccb = Component.text(tasks.isEmpty() ? "Es gibt keine Tasks!" : "Tasks: ");
            if (tasks.isEmpty()) {
                ccb = ccb.color(NamedTextColor.RED);
            } else {
                ccb = ccb.color(NamedTextColor.GREEN);
            }
            for (var task : tasks) {
                ccb = ccb.append(Component.text("\n - ").color(NamedTextColor.YELLOW)).append(Component.text(task).color(NamedTextColor.AQUA));
                // sb.append("\n§e- §b").append(task);
            }
            ctx.getSource().sendMessage(ccb);
            return 0;
        }));
    }

}

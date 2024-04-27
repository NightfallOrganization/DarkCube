/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.sumo;

import eu.cloudnetservice.driver.service.ServiceTask;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.arguments.ServiceTaskArgument;

import java.util.Set;
import java.util.stream.Stream;

public class CommandAddTask extends LobbyCommandExecutor {

    public CommandAddTask() {
        super("addTask", b -> b.then(Commands.argument("task", new ServiceTaskArgument() {
            @Override protected Stream<String> tasksStream() {
                return super.tasksStream().filter(t -> !Lobby.getInstance().getDataManager().getSumoTasks().contains(t));
            }
        }).executes(ctx -> {
            ServiceTask task = ServiceTaskArgument.getServiceTask(ctx, "task");
            Set<String> tasks = Lobby.getInstance().getDataManager().getSumoTasks();
            if (tasks.contains(task.name())) {
                ctx.getSource().sendMessage(Component.text("Dieser Task ist bereits festgelegt!").color(NamedTextColor.RED));
                return 0;
            }
            tasks.add(task.name());
            Lobby.getInstance().getDataManager().setSumoTasks(tasks);
            ctx.getSource().sendMessage(Component.text("Task erfolgreich eingespeichert!").color(NamedTextColor.GREEN));
            return 0;
        })));
    }

}
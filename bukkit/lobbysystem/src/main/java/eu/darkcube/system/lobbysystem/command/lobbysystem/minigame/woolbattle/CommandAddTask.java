/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import java.util.Set;
import java.util.stream.Stream;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.arguments.ServiceTaskArgument;
import net.md_5.bungee.api.ChatColor;

public class CommandAddTask extends LobbyCommandExecutor {

	public CommandAddTask() {
		super("addTask", b -> b.then(Commands.argument("task", new ServiceTaskArgument() {
			@Override
			protected Stream<String> tasksStream() {
				return super.tasksStream().filter(t -> !Lobby.getInstance().getDataManager()
						.getWoolBattleTasks().contains(t));
			}
		}).executes(ctx -> {
			ServiceTask task = ServiceTaskArgument.getServiceTask(ctx, "task");
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			if (tasks.contains(task.getName())) {
				ctx.getSource().sendFeedback(
						new CustomComponentBuilder("Dieser Task ist bereits festgelegt!")
								.color(ChatColor.RED).create(),
						true);
				return 0;
			}
			tasks.add(task.getName());
			Lobby.getInstance().getDataManager().setWoolBattleTasks(tasks);
			ctx.getSource()
					.sendFeedback(new CustomComponentBuilder("Task erfolgreich eingespeichert!")
							.color(ChatColor.GREEN).create(), true);
			return 0;
		})));
	}

}

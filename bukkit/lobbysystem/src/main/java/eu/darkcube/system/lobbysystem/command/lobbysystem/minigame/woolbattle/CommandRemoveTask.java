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

public class CommandRemoveTask extends LobbyCommandExecutor {

	public CommandRemoveTask() {
		super("removeTask", b -> b.then(Commands.argument("task", new ServiceTaskArgument() {
			@Override
			protected Stream<String> tasksStream() {
				return super.tasksStream().filter(
						t -> Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(t));
			}
		}).executes(ctx -> {
			ServiceTask task = ServiceTaskArgument.getServiceTask(ctx, "task");
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			tasks.remove(task.getName());
			Lobby.getInstance().getDataManager().setWoolBattleTasks(tasks);
			ctx.getSource().sendFeedback(new CustomComponentBuilder("Task erfolgreich entfernt!")
					.color(ChatColor.GREEN).create(), true);
			return 0;
		})));
	}

}

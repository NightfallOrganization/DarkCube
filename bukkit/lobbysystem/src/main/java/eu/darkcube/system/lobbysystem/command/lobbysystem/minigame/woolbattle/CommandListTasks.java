package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import java.util.Set;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import net.md_5.bungee.api.ChatColor;

public class CommandListTasks extends LobbyCommandExecutor {

	public CommandListTasks() {
		super("listTasks", b -> b.executes(ctx -> {
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			CustomComponentBuilder ccb = new CustomComponentBuilder(
					tasks.isEmpty() ? "Es gibt keine Tasks!" : "Tasks: ");
			if (tasks.isEmpty()) {
				ccb.color(ChatColor.RED);
			} else {
				ccb.color(ChatColor.GREEN);
			}
			for (String task : tasks) {
				ccb.append("\n- ").color(ChatColor.YELLOW).append(task).color(ChatColor.AQUA);
				// sb.append("\n§e- §b").append(task);
			}
			return 0;
		}));
	}

}

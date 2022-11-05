package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.CommandArgument;

public class CommandRemoveTask extends Command {
	public CommandRemoveTask() {
		super(Lobby.getInstance(), "removeTask", new Command[0], "Entfernt eine Task", CommandArgument.CLOUD_TASK);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return new ArrayList<>(Lobby.getInstance().getDataManager().getWoolBattleTasks());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			CloudNetDriver w = CloudNetDriver.getInstance();
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			if (!w.getServiceTaskProvider().isServiceTaskPresent(args[0]) && !tasks.contains(args[0])) {
				sender.sendMessage("§cEs gibt keine CloudTask mit dem Namen " + args[0]);
				return true;
			}
			String task = tasks.contains(args[0]) ? args[0]
					: w.getServiceTaskProvider().getServiceTask(args[0]).getName();
			tasks.remove(task);
			Lobby.getInstance().getDataManager().setWoolBattleTasks(tasks);
			sender.sendMessage("§aTask erfolgreich entfernt!");
			return true;
		}
		return false;
	}
}

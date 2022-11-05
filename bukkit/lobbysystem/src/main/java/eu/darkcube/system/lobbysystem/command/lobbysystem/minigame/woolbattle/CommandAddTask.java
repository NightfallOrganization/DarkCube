package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.CommandArgument;

public class CommandAddTask extends Command {

	public CommandAddTask() {
		super(Lobby.getInstance(), "addTask", new Command[0], "Fügt eine Task hinzu", CommandArgument.CLOUD_TASK);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return CloudNetDriver.getInstance().getServiceTaskProvider().getPermanentServiceTasks().stream().filter(
					t -> t.getProcessConfiguration().getEnvironment() == ServiceEnvironmentType.MINECRAFT_SERVER)
					.map(t -> t.getName()).filter(t -> t.startsWith(args[0]))
					.filter(t -> !Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(t))
					.collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			CloudNetDriver w = CloudNetDriver.getInstance();
			if (!w.getServiceTaskProvider().isServiceTaskPresent(args[0])) {
				sender.sendMessage("§cEs gibt keine CloudTask mit dem Namen " + args[0]);
				return true;
			}
			String task = w.getServiceTaskProvider().getServiceTask(args[0]).getName();
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			if (tasks.contains(task)) {
				sender.sendMessage("§cDiese Task ist bereits festgelegt!");
				return true;
			}
			tasks.add(task);
			Lobby.getInstance().getDataManager().setWoolBattleTasks(tasks);
			sender.sendMessage("§aTask erfolgreich gespeichert!");
			return true;
		}
		return false;
	}
}

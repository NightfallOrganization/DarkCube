package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;

public class CommandListTasks extends Command {

	public CommandListTasks() {
		super(Lobby.getInstance(), "listTasks", new Command[0], "Listet alle Tasks auf");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		StringBuilder b = new StringBuilder();
		for (String task : Lobby.getInstance().getDataManager().getWoolBattleTasks()) {
			b.append("\n§e- §b").append(task);
		}
		if(b.length() == 0) {
			sender.sendMessage("§cEs gibt keine Tasks!");
		} else {
			sender.sendMessage("§aTasks: " + b);
		}
		return true;
	}
}

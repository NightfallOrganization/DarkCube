package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandAddTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandListTasks;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandRemoveTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandSetSpawn;

public class CommandWoolBattle extends Command {

	public CommandWoolBattle() {
		super(Lobby.getInstance(), "woolbattle", new Command[] { new CommandSetSpawn(), new CommandListTasks(),
				new CommandAddTask(), new CommandRemoveTask() }, "WoolBattle Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		return false;
	}

}

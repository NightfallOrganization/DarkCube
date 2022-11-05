package eu.darkcube.system.lobbysystem.command.lobbysystem;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.CommandWoolBattle;

public class CommandMinigame  extends Command {

	public CommandMinigame() {
		super(Lobby.getInstance(), "minigame", new Command[] {
				new CommandWoolBattle()
		}, "MiniGame Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}

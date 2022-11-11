package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandCreateTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandDeleteMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandDeleteTeam;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListMaps;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandListTeams;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandLoadWorld;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandMap;
import eu.darkcube.minigame.woolbattle.command.woolbattle.CommandTeam;
import eu.darkcube.system.commandapi.Command;

public class CommandWoolBattle extends Command {

	public CommandWoolBattle() {
		super(Main.getInstance(), "woolbattle", new Command[] {
				new CommandTeam(), new CommandCreateTeam(), new CommandDeleteTeam(), new CommandListTeams(),
				new CommandCreateMap(), new CommandDeleteMap(), new CommandMap(), new CommandListMaps(),
				new CommandLoadWorld()
		}, "WoolBattle Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}

}

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.Command;

public class CommandDeleteTeam extends Command {

	public CommandDeleteTeam() {
		super(Main.getInstance(), "deleteTeam", new Command[] {}, "Löscht ein Team", CommandArgument.TEAM);
	}
	
	@Override
	public List<String> onTabComplete(String[] args) {
		if(args.length == 1) {
			return Arrays.toSortedStringList(TeamType.values(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			TeamType team = TeamType.byDisplayNameKey(args[0]);
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + args[0] + "' gefunden werden.");
				return true;
			}
			team.delete();
			sender.sendMessage("§aDu hast das Team '" + team.getDisplayNameKey() + "' gelöscht!");
			return true;
		}
		return false;
	}
}
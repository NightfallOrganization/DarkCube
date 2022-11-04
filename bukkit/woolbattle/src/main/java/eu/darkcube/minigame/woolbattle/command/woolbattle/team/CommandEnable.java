package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandEnable extends SubCommand {

	public CommandEnable() {
		super(Main.getInstance(), "enable", new Command[0], "Aktiviert das Team");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			TeamType team = TeamType.byDisplayNameKey(getSpaced());
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
				return true;
			}
			if (team.isEnabled()) {
				sender.sendMessage("§cDieses Team ist bereits aktiviert!");
				return true;
			}
			team.setEnabled(true);
			sender.sendMessage("§7Du hast das Team '" + team.getDisplayNameKey() + "' aktiviert!");
			return true;
		}
		return false;
	}
}
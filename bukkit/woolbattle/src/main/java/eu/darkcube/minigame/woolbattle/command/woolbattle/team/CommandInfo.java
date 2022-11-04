package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandInfo extends SubCommand {

	public CommandInfo() {
		super(Main.getInstance(), "info", new Command[0], "Ruft informationen des Teams ab");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			TeamType team = TeamType.byDisplayNameKey(getSpaced());
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
				return true;
			}
			StringBuilder b = new StringBuilder();
			b.append("§aTeam: " + team.getDisplayNameKey()).append("\nNamensfarbe: §").append(team.getNameColor())
					.append(ChatColor.getByChar(team.getNameColor()).name()).append("\n§aWollfarbe: ")
					.append(DyeColor.getByData(team.getWoolColor())).append("\nSortierung: ").append(team.getWeight())
					.append("\nMaximale Spieleranzahl: ").append(team.getMaxPlayers()).append("\nAktiviert: ")
					.append(team.isEnabled());
			sender.sendMessage(b.toString());
			return true;
		}
		return false;
	}

}
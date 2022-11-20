package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ColorUtil;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetNameColor extends SubCommand {

	public CommandSetNameColor() {
		super(WoolBattle.getInstance(), "setNameColor", new Command[0], "Setzt die Namensfarbe", CommandArgument.NAME_COLOR);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(ChatColor.values(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			TeamType team = TeamType.byDisplayNameKey(getSpaced());
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
				return true;
			}
			ChatColor chatColor = ChatColor.getByChar(ColorUtil.byChatColor(args[0]));
			if (chatColor == null) {
				sender.sendMessage("§cBitte gib eine gültige Namenfarbe an!");
				return true;
			}
			team.setNameColor(chatColor);
			sender.sendMessage("§7Du hast die Namensfarbe des Teams " + team.getDisplayNameKey() + " zu " + chatColor
					+ chatColor.name() + "§7 geändert.");
			return true;
		}
		return false;
	}
}
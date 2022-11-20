package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ColorUtil;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetWoolColor extends SubCommand {

	public CommandSetWoolColor() {
		super(WoolBattle.getInstance(), "setWoolColor", new Command[0], "Setzt die Wollfarbe", CommandArgument.WOOL_COLOR);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(DyeColor.values(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			TeamType team = TeamType.byDisplayNameKey(getSpaced());
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
				return true;
			}
			DyeColor dyeColor = DyeColor.getByData(ColorUtil.byDyeColor(args[0]));
			if (dyeColor == null) {
				sender.sendMessage("§cBitte gib eine gültige Wollfarbe an!");
				return true;
			}
			team.setWoolColor(dyeColor);
			sender.sendMessage("§7Du hast die Wollfarbe des Teams " + team.getDisplayNameKey() + " zu "
					+ dyeColor.name() + "§7 geändert.");
			return true;
		}
		return false;
	}
}

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.MaterialAndId;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetIcon extends SubCommand {

	public CommandSetIcon() {
		super(WoolBattle.getInstance(), "setIcon", new Command[0], "Setzt das Icon der Map", CommandArgument.ICON);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			List<Material> mats = Arrays.asList(Material.values());
			mats.remove(Material.AIR);
			return Arrays.toSortedStringList(mats, args[0].toUpperCase());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = WoolBattle.getInstance().getMapManager().getMap(getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
				return true;
			}
			MaterialAndId icon = MaterialAndId.fromString(args[0]);
			if (icon == null || args[0].startsWith("AIR")) {
				sender.sendMessage("§cDieses Icon ist nicht gültig");
				return true;
			}
			map.setIcon(icon);
			sender.sendMessage("§aDie Map '" + map.getName() + "' hat nun das Icon '" + icon + "'.");
			return true;
		}
		return false;
	}
}
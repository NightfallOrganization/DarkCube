package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.Command;

public class CommandDeleteMap extends Command {

	public CommandDeleteMap() {
		super(Main.getInstance(), "deleteMap", new Command[0], "Löscht eine Map", CommandArgument.MAP);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Main.getInstance().getMapManager().getMaps(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = Main.getInstance().getMapManager().getMap(args[0]);
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + args[0] + "'gefunden werden.");
				return true;
			}
			map.delete();
			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' gelöscht!");
			return true;
		}
		return false;
	}
}
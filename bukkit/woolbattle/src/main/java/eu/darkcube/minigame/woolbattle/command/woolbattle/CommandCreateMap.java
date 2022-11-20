package eu.darkcube.minigame.woolbattle.command.woolbattle;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.Command;

public class CommandCreateMap extends Command {

	public CommandCreateMap() {
		super(WoolBattle.getInstance(), "createMap", new Command[0], "Erstellt eine Map", CommandArgument.MAP);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
			if (map != null) {
				sender.sendMessage("§cEs gibt bereits eine Map mit dem Namen '" + args[0] + "'.");
				return true;
			}
			map = WoolBattle.getInstance().getMapManager().createMap(args[0]);
			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' erstellt!");
			return true;
		}
		return false;
	}

}

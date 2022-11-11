package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetDeathHeight extends SubCommand {

	public CommandSetDeathHeight() {
		super(Main.getInstance(), "setDeathHeight", new Command[0], "Setzt die Todeshöhe der Map",
				CommandArgument.DEATHHEIGHT);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			Map map = Main.getInstance().getMapManager().getMap(this.getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + this.getSpaced() + "'gefunden werden.");
				return true;
			}
			try {
				int height = Integer.parseInt(args[0]);
				map.setDeathHeight(height);
				sender.sendMessage("§aTodeshöhe umgesetzt!");
				return true;
			} catch (Exception ex) {
				sender.sendMessage("§cUngültige Zahl: " + args[0]);
				return true;
			}
		}
		return false;
	}

}

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandInfo extends SubCommand {

	public CommandInfo() {
		super(Main.getInstance(), "info", new Command[0], "Ruft die Informationen der Map ab");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			Map map = Main.getInstance().getMapManager().getMap(getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
				return true;
			}
			StringBuilder b = new StringBuilder();
			b.append("§aMap: ").append(map.getName()).append("\nIcon: ").append(map.getIcon()).append("DeathHeight")
					.append(map.getDeathHeight()).append("\nAktiviert: ").append(map.isEnabled());
			sender.sendMessage(b.toString());
			return true;
		}
		return false;
	}

}
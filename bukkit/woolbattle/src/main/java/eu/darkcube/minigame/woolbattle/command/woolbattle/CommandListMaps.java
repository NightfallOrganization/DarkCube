package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.Collection;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.Command;

public class CommandListMaps extends Command {

	public CommandListMaps() {
		super(Main.getInstance(), "listMaps", new Command[0], "Listet alle Maps auf");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Collection<? extends Map> maps = Main.getInstance().getMapManager().getMaps();
		if (maps.size() == 0) {
			sender.sendMessage("§cEs sind keine Teams erstellt!");
			return true;
		}
		StringBuilder b = new StringBuilder();
		for (Map m : maps) {
			b.append("§7- Name: '§5").append(m.getName()).append("§7', DeathHeight: §5").append(m.getDeathHeight())
					.append("§7, Icon: ").append(m.getIcon()).append("\n");
		}
		sender.sendMessage(b.toString());
		return true;
	}

}
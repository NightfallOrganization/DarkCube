package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.CommandArgument;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.DataManager;

public class CommandSetPos2 extends Command {
	public CommandSetPos2() {
		super(Lobby.getInstance(), "setPos2", new Command[0], "Setzt die 2. Position der Border",
				CommandArgument.MAKE_NICE_LOCATION);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.asList("true", "false").stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Location loc = p.getLocation();
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("true")) {
					loc = Locations.getNiceLocation(loc);
					p.teleport(loc);
				}
			}
			DataManager data = Lobby.getInstance().getDataManager();
			Border border = data.getBorder();
			border = new Border(border.getShape(), border.getRadius(), border.getLoc1(), loc);
			data.setBorder(border);
			p.sendMessage("§aPosition 2 neugesetzt!");
			return true;
		}
		return false;
	}
}

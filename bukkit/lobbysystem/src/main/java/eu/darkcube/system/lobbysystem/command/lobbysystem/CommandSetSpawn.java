package eu.darkcube.system.lobbysystem.command.lobbysystem;

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

public class CommandSetSpawn extends Command {

	public CommandSetSpawn() {
		super(Lobby.getInstance(), "setSpawn", new Command[0], "Setzt den Spawn", CommandArgument.MAKE_NICE_LOCATION);
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
			Lobby.getInstance().getDataManager().setSpawn(loc);
			p.sendMessage("§aSpawn neugesetzt!");
			return true;
		}
		return false;
	}
}
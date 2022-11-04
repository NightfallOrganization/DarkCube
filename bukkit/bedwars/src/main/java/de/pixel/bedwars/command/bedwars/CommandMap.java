package de.pixel.bedwars.command.bedwars;

import java.util.List;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.command.bedwars.map.CommandSetBed;
import de.pixel.bedwars.command.bedwars.map.CommandSetIcon;
import de.pixel.bedwars.command.bedwars.map.CommandSetSpawn;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.SpacedCommand;

public class CommandMap extends SpacedCommand {

	public CommandMap() {
		super(Main.getInstance(), "map", new SubCommand[] {
				new CommandSetIcon(), new CommandSetSpawn(), new CommandSetBed()
		}, "Map Hauptcommand", new Argument("Map", "Die Map"));
	}
	
	@Override
	public List<String> onTabComplete(String[] args) {
		if(args.length == 1) {
			return Arrays.toSortedStringList(Map.getMaps(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}

}

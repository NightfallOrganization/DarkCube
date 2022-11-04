package de.pixel.bedwars.command.bedwars.map;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.command.bedwars.CommandTeam;
import de.pixel.bedwars.map.Map;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Arrays;
import de.pixel.bedwars.util.Locations;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetSpawn extends SubCommand {

	public CommandSetSpawn() {
		super(Main.getInstance(), "setSpawn", new Command[0], "Setzt den Spawn für ein Team",
				new Argument("team", "Das Team"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		if (args.length == 1) {
			Map map = Map.getMap(getSpaced());
			if (map == null) {
				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
				return true;
			}
			Team team = CommandTeam.doTeam(args[0], sender);
			if (team == null) {
				return true;
			}
			map.setSpawn(team.getInvisiblePlayerName(), Locations.getNiceLocation(((Player) sender).getLocation()));
			((Player)sender).teleport(Locations.getNiceLocation(((Player) sender).getLocation()));
			sender.sendMessage("§7Spawn neugesetzt!");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(Team.getAllTeams(), args[0]);
		}
		return super.onTabComplete(args);
	}

}

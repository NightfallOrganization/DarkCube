package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandSetSpawn extends SubCommand {

	public CommandSetSpawn() {
		super(WoolBattle.getInstance(), "setSpawn", new Command[0],
						"Setzt den Spawn eines Teams", CommandArgument.MAP,
						CommandArgument.ARGUMENT_MAKE_NICE);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
		}
		if (args.length == 2) {
			return Arrays.toSortedStringList(Arrays.asList(true, false), args[1]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length != 1 && args.length != 2) {
				return false;
			}
			Player p = (Player) sender;
			Team team = WoolBattle.getInstance().getTeamManager().getTeam(TeamType.byDisplayNameKey(getSpaced()));
			if (team == null || team.getType().isDeleted()) {
				p.sendMessage("§cEs konnte kein Team mit dem Namen '"
								+ getSpaced() + "' gefunden werden.");
				p.sendMessage("§aNach dem erstellen eines Teams muss der Server neugestartet werden um Spawns setzen zu können!");
				return true;
			}
			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
			if (map == null) {
				p.sendMessage("§cEs konnte keine Map mit dem Namen '" + args[0]
								+ "'gefunden werden.");
				return true;
			}
			Location loc = p.getLocation();
			if (args.length == 2) {
				String mn = args[1];
				if (mn.equalsIgnoreCase("true")) {
					loc = Locations.getNiceLocation(loc);
					p.teleport(loc);
				}
			}
			p.sendMessage("§7Du hast den Spawn für die Map '" + map.getName()
							+ "' neugesetzt!");
			map.setSpawn(team.getType().getDisplayNameKey(), loc);
			return true;
		}
		sender.sendMessage(Message.NO_PLAYER.getServerMessage());
		return true;
	}
}
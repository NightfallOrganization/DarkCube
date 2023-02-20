/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandSetSpawn extends WBCommandExecutor {
	public CommandSetSpawn() {
		super("setSpawn", b -> b.then(Commands.argument("map", MapArgument.mapArgument())
				.executes(CommandSetSpawn::set)));
	}

	private static int set(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		TeamType team = TeamArgument.getTeam(ctx, "team");
		Map map = MapArgument.getMap(ctx, "map");
		Player player = ctx.getSource().asPlayer();
		Location loc = Locations.getNiceLocation(player.getLocation());
		player.teleport(loc);
		map.setSpawn(team.getDisplayNameKey(), loc);
		player.sendMessage("§7Du hast den Spawn für die Map '" + map.getName() + "' neu gesetzt!");
		return 0;
	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (sender instanceof Player) {
	//			if (args.length != 1 && args.length != 2) {
	//				return false;
	//			}
	//			Player p = (Player) sender;
	//			Team team = WoolBattle.getInstance().getTeamManager().getTeam(TeamType.byDisplayNameKey(getSpaced()));
	//			if (team == null || team.getType().isDeleted()) {
	//				p.sendMessage("§cEs konnte kein Team mit dem Namen '"
	//								+ getSpaced() + "' gefunden werden.");
	//				p.sendMessage("§aNach dem erstellen eines Teams muss der Server neugestartet werden um Spawns setzen zu können!");
	//				return true;
	//			}
	//			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
	//			if (map == null) {
	//				p.sendMessage("§cEs konnte keine Map mit dem Namen '" + args[0]
	//								+ "'gefunden werden.");
	//				return true;
	//			}
	//			Location loc = p.getLocation();
	//			if (args.length == 2) {
	//				String mn = args[1];
	//				if (mn.equalsIgnoreCase("true")) {
	//					loc = Locations.getNiceLocation(loc);
	//					p.teleport(loc);
	//				}
	//			}
	//			p.sendMessage("§7Du hast den Spawn für die Map '" + map.getName()
	//							+ "' neugesetzt!");
	//			map.setSpawn(team.getType().getDisplayNameKey(), loc);
	//			return true;
	//		}
	//		sender.sendMessage(Message.NO_PLAYER.getServerMessage());
	//		return true;
	//	}
}

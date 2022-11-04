package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class ForceMapCommand extends PServerExecutor {

	public ForceMapCommand() {
		super("forcemap", new String[0],
						b -> b.then(Commands.argument("map", MapArgument.mapArgument()).executes(context -> {
							Map map = MapArgument.getMap(context, "map");
							Main.getInstance().baseMap = map;
							context.getSource().sendFeedback(Message.WOOLBATTLE_FORCEMAP.getMessage(context.getSource(), map.getName()), true);
							return 0;
						})));
	}

}

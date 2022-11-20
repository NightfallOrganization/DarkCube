package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class TimerCommand extends PServerExecutor {

	public TimerCommand() {
		super("timer", new String[0],
						b -> b.then(Commands.argument("timer", IntegerArgumentType.integer(0, 10000)).executes(context -> {
							int timer = IntegerArgumentType.getInteger(context, "timer");
							CommandSource source = context.getSource();
							WoolBattle.getInstance().getLobby().setOverrideTimer(timer <= 1
											? 2
											: timer * 20);
							source.sendFeedback(Message.WOOLBATTLE_TIMER.getMessage(source, timer), true);
							return 0;
						})));
	}

}

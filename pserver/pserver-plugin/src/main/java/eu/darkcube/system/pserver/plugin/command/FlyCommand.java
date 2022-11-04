package eu.darkcube.system.pserver.plugin.command;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class FlyCommand extends PServerExecutor {

	public FlyCommand() {
		super("fly", new String[0],
						b -> b.executes(context -> {
							return fly(context, Collections.singleton(context.getSource().asPlayer()), null);
						}).then(Commands.argument("targets", EntityArgument.players()).executes(context -> {
							return fly(context, EntityArgument.getPlayers(context, "targets"), null);
						}).then(Commands.argument("state", EnumArgument.enumArgument(State.values(), s -> new String[] {
										s.name().toLowerCase()
						})).executes(context -> {
							return fly(context, EntityArgument.getPlayers(context, "targets"), EnumArgument.getEnumArgument(context, "state", State.class).state);
						}))));
	}

	private static int fly(CommandContext<CommandSource> context,
					Collection<Player> targets, Boolean state) {
		targets.forEach(p -> {
			boolean newState = state == null ? !p.getAllowFlight() : state;
			if (p.getAllowFlight() != newState) {
				p.setAllowFlight(newState);
				context.getSource().sendFeedback(Message.FLIGHT_CHANGED_SINGLE.getMessage(context.getSource(), p.getName(), p.getAllowFlight()
								? Message.ON.getMessageString(context.getSource().getSource())
								: Message.OFF.getMessageString(context.getSource().getSource())), true);
			}
		});
		return 0;
	}

	public static enum State {
		ON(true),
		OFF(false);

		private final boolean state;

		private State(boolean state) {
			this.state = state;
		}
	}
}

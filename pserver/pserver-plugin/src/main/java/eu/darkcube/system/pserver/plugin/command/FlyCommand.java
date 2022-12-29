/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

public class FlyCommand extends PServerExecutor {

	public FlyCommand() {
		super("fly", new String[0], b -> b.executes(context -> {
			return fly(context, Collections.singleton(context.getSource().asPlayer()), null);
		}).then(Commands.argument("targets", EntityArgument.players()).executes(context -> {
			return fly(context, EntityArgument.getPlayers(context, "targets"), null);
		}).then(Commands.argument("state", EnumArgument.enumArgument(State.values(),
				s -> new String[] {s.name().toLowerCase()})).executes(context -> {
			return fly(context, EntityArgument.getPlayers(context, "targets"),
					EnumArgument.getEnumArgument(context, "state", State.class).state);
		}))));
	}

	private static int fly(CommandContext<CommandSource> context, Collection<Player> targets,
			Boolean state) {
		targets.forEach(p -> {
			boolean newState = state == null ? !p.getAllowFlight() : state;
			if (p.getAllowFlight() != newState) {
				p.setAllowFlight(newState);
				context.getSource().sendMessage(Message.FLIGHT_CHANGED_SINGLE, p.getName(),
						p.getAllowFlight()
								? Message.ON.getMessageString(context.getSource().getSource())
								: Message.OFF.getMessageString(context.getSource().getSource()));
			}
		});
		return 0;
	}

	public enum State {
		ON(true), OFF(false);

		private final boolean state;

		State(boolean state) {
			this.state = state;
		}
	}
}

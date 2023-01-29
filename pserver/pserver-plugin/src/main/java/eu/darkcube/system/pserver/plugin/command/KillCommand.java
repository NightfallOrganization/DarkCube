/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Collections;

public class KillCommand extends PServerExecutor {

	public KillCommand() {
		super("kill", new String[0], b -> b.executes(context -> {
			return kill(context, Collections.singleton(context.getSource().assertIsEntity()));
		}).then(Commands.argument("targets", EntityArgument.entities()).executes(context -> {
			return kill(context, EntityArgument.getEntities(context, "targets"));
		})));
	}

	private static int kill(CommandContext<CommandSource> context,
			Collection<? extends Entity> entities) {
		entities.forEach(entity -> {
			if (entity instanceof LivingEntity) {
				((LivingEntity) entity).setHealth(0);
			} else {
				entity.remove();
			}
			context.getSource().sendMessage(Message.KILLED_ENTITY, entity.getName());
		});
		return 0;
	}

}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.arguments;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ServiceTaskArgument implements ArgumentType<ServiceTask> {

	private static final DynamicCommandExceptionType TASK_NOT_PRESENT =
			Messages.SERVICE_TASK_NOT_PRESENT.newDynamicCommandExceptionType();

	public static ServiceTaskArgument serviceTask() {
		return new ServiceTaskArgument();
	}

	public static ServiceTask getServiceTask(CommandContext<CommandSource> ctx, String name) {
		return ctx.getArgument(name, ServiceTask.class);
	}

	@Override
	public ServiceTask parse(StringReader r) throws CommandSyntaxException {
		CloudNetDriver d = CloudNetDriver.getInstance();
		String t = r.readString();
		if (!d.getServiceTaskProvider().isServiceTaskPresent(t)) {
			throw TASK_NOT_PRESENT.createWithContext(r, t);
		}
		return d.getServiceTaskProvider().getServiceTask(t);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(tasksStream(), builder);
	}

	protected Stream<String> tasksStream() {
		return CloudNetDriver.getInstance().getServiceTaskProvider().getPermanentServiceTasks()
				.stream().filter(t -> t.getProcessConfiguration().getEnvironment()
						== ServiceEnvironmentType.MINECRAFT_SERVER).map(t -> t.getName());

	}
}

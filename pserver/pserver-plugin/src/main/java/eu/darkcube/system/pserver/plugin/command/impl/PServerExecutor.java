package eu.darkcube.system.pserver.plugin.command.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.pserver.plugin.PServerPlugin;

public abstract class PServerExecutor extends CommandExecutor {

	public static final Collection<String> COMMAND_NAMES = new HashSet<>();
	public static final Collection<String> TOTAL_COMMAND_NAMES = new HashSet<>();

	public PServerExecutor(String name, String[] aliases,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super(PServerPlugin.COMMAND_PREFIX, name, PServerPlugin.COMMAND_PREFIX + "." + name,
				aliases, argumentBuilder);
		COMMAND_NAMES.add(name);
		for (String n : getNames()) {
			TOTAL_COMMAND_NAMES.add(n);
			TOTAL_COMMAND_NAMES.add(PServerPlugin.COMMAND_PREFIX + ":" + n);
		}
	}
}

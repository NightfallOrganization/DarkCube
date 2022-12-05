package eu.darkcube.system.lobbysystem.command;

import java.util.function.Consumer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;

public class LobbyCommandExecutor extends CommandExecutor {

	public LobbyCommandExecutor(String name, String[] aliases,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super("lobby", name, aliases, argumentBuilder);
	}

	public LobbyCommandExecutor(String name,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(name, new String[0], argumentBuilder);
	}

}

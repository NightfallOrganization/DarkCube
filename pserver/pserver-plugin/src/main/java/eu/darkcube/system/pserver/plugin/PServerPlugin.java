package eu.darkcube.system.pserver.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.plugin.command.CommandBlockCommand;
import eu.darkcube.system.pserver.plugin.command.CommandsCommand;
import eu.darkcube.system.pserver.plugin.command.DifficultyCommand;
import eu.darkcube.system.pserver.plugin.command.EffectCommand;
import eu.darkcube.system.pserver.plugin.command.FlyCommand;
import eu.darkcube.system.pserver.plugin.command.GameModeCommand;
import eu.darkcube.system.pserver.plugin.command.KillCommand;
import eu.darkcube.system.pserver.plugin.command.PServerCommand;
import eu.darkcube.system.pserver.plugin.command.StopCommand;
import eu.darkcube.system.pserver.plugin.command.TeleportCommand;
import eu.darkcube.system.pserver.plugin.command.UsersCommand;
import eu.darkcube.system.pserver.plugin.link.LinkManager;
import eu.darkcube.system.pserver.plugin.link.luckperms.LuckPermsLink;
import eu.darkcube.system.pserver.plugin.link.woolbattle.WoolBattleLink;
import eu.darkcube.system.pserver.plugin.listener.CommandBlockModifyListener;
import eu.darkcube.system.pserver.plugin.listener.InactivityListener;
import eu.darkcube.system.pserver.plugin.listener.UserCacheListener;
import eu.darkcube.system.pserver.plugin.user.UserCache;
import eu.darkcube.system.pserver.plugin.user.UserManager;

public class PServerPlugin extends DarkCubePlugin {

	private static PServerPlugin instance;

	public static final String COMMAND_PREFIX = "pserver";

	private final LinkManager linkManager = new LinkManager();
	private final Path workingDirectory;

	public PServerPlugin() {
		instance = this;
		workingDirectory = Paths.get("pserver");
		try {
			if (!Files.exists(workingDirectory))
				Files.createDirectory(workingDirectory);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onEnable() {

		UserCache.load();

		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties", Message.KEY_MODIFIER);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties", Message.KEY_MODIFIER);

			List<String> messageKeys = new ArrayList<>();
			messageKeys.addAll(Arrays.asList(Message.values()).stream().map(Message::getKey).collect(Collectors.toList()));
			Arrays.asList(Item.values()).stream().map(Item::getMessageKeys).forEach(messageKeys::addAll);

			Language.validateEntries(messageKeys.toArray(new String[0]), Message.KEY_MODIFIER);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

//		DeprecatedUser.register();
		UserManager.register();

		CommandAPI api = CommandAPI.getInstance();
		api.register(new TeleportCommand());
		api.register(new StopCommand());
		api.register(new GameModeCommand());
		api.register(new FlyCommand());
		api.register(new DifficultyCommand());
		api.register(new KillCommand());
		api.register(new CommandsCommand());
		api.register(new EffectCommand());
		api.register(new UsersCommand());
		api.register(new CommandBlockCommand());

		new InactivityListener();
		new UserCacheListener();
		new CommandBlockModifyListener();

		PServerProvider.getInstance().setPServerCommand(new PServerCommand());

		this.linkManager.addLink(() -> new LuckPermsLink());
		this.linkManager.addLink(() -> new WoolBattleLink());
	}

	@Override
	public void onDisable() {

		linkManager.unregisterLinks();

		UserManager.unregister();
//		DeprecatedUser.unregister();

		UserCache.unload();
	}

	public Path getWorkingDirectory() {
		return workingDirectory;
	}

	public static PServerPlugin getInstance() {
		return instance;
	}
}
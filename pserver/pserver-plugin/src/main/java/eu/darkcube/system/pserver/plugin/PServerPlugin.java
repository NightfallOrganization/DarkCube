/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.plugin.command.*;
import eu.darkcube.system.pserver.plugin.link.LinkManager;
import eu.darkcube.system.pserver.plugin.link.luckperms.LuckPermsLink;
import eu.darkcube.system.pserver.plugin.link.woolbattle.WoolBattleLink;
import eu.darkcube.system.pserver.plugin.listener.CommandBlockModifyListener;
import eu.darkcube.system.pserver.plugin.listener.InactivityListener;
import eu.darkcube.system.pserver.plugin.listener.UserCacheListener;
import eu.darkcube.system.pserver.plugin.user.UserCache;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import eu.darkcube.system.util.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PServerPlugin extends DarkCubePlugin {

	public static final String COMMAND_PREFIX = "pserver";
	private static PServerPlugin instance;
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

	public static PServerPlugin getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {

		linkManager.unregisterLinks();

		UserManager.unregister();
		//		DeprecatedUser.unregister();

		UserCache.unload();
	}

	@Override
	public void onEnable() {

		UserCache.load();

		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties",
					Message.KEY_MODIFIER);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties",
					Message.KEY_MODIFIER);

			List<String> messageKeys = Arrays.stream(Message.values()).map(Message::getKey)
					.collect(Collectors.toList());
			Arrays.stream(Item.values()).map(Item::getMessageKeys).forEach(messageKeys::addAll);

			Language.validateEntries(messageKeys.toArray(new String[0]), Message.KEY_MODIFIER);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

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
		//		api.register(new UsersCommand());
		api.register(new CommandBlockCommand());

		new InactivityListener();
		new UserCacheListener();
		new CommandBlockModifyListener();

		PServerProvider.getInstance().setPServerCommand(new PServerCommand());

		this.linkManager.addLink(LuckPermsLink::new);
		this.linkManager.addLink(WoolBattleLink::new);
	}

	public Path getWorkingDirectory() {
		return workingDirectory;
	}
}

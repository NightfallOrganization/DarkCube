/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.pserver.common.PServerProvider;
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
import eu.darkcube.system.pserver.plugin.link.LinkManager;
import eu.darkcube.system.pserver.plugin.link.luckperms.LuckPermsLink;
import eu.darkcube.system.pserver.plugin.link.woolbattle.WoolBattleLink;
import eu.darkcube.system.pserver.plugin.listener.CommandBlockModifyListener;
import eu.darkcube.system.pserver.plugin.listener.InactivityListener;
import eu.darkcube.system.pserver.plugin.listener.UserCacheListener;
import eu.darkcube.system.pserver.plugin.user.UserCache;
import eu.darkcube.system.pserver.plugin.user.UserManager;
import eu.darkcube.system.pserver.plugin.util.OwnerCache;
import eu.darkcube.system.util.Language;

public class PServerPlugin extends DarkCubePlugin {

    public static final String COMMAND_PREFIX = "pserver";
    private static PServerPlugin instance;
    private final LinkManager linkManager = new LinkManager();
    private final Path workingDirectory;
    private final OwnerCache ownerCache = new OwnerCache();

    public PServerPlugin() {
        super("pserver-features");
        instance = this;
        workingDirectory = Paths.get("pserver");
        try {
            if (!Files.exists(workingDirectory)) Files.createDirectory(workingDirectory);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static PServerPlugin instance() {
        return instance;
    }

    @Override
    public void onDisable() {

        linkManager.unregisterLinks();

        UserManager.unregister();
        ownerCache.unregister();
        UserCache.unload();
    }

    @Override
    public void onEnable() {

        UserCache.load();

        try {
            Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties", Message.KEY_MODIFIER);
            Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties", Message.KEY_MODIFIER);

            List<String> messageKeys = Arrays.stream(Message.values()).map(Message::key).collect(Collectors.toList());
            Arrays.stream(Item.values()).map(Item::getMessageKeys).forEach(messageKeys::addAll);

            Language.validateEntries(messageKeys.toArray(new String[0]), Message.KEY_MODIFIER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        UserManager.register();

        CommandAPI api = CommandAPI.instance();
        api.register(new TeleportCommand());
        api.register(new StopCommand());
        api.register(new GameModeCommand());
        api.register(new FlyCommand());
        api.register(new DifficultyCommand());
        api.register(new KillCommand());
        api.register(new CommandsCommand());
        api.register(new EffectCommand());
        // api.register(new UsersCommand());
        api.register(new CommandBlockCommand());

        new InactivityListener();
        new UserCacheListener();
        new CommandBlockModifyListener();

        ownerCache.register();

        PServerProvider.instance().setPServerCommand(new PServerCommand());

        this.linkManager.addLink(LuckPermsLink::new);
        this.linkManager.addLink(WoolBattleLink::new);
    }

    public OwnerCache ownerCache() {
        return ownerCache;
    }

    public Path getWorkingDirectory() {
        return workingDirectory;
    }
}

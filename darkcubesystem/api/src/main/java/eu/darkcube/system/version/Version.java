/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.inventoryapi.item.ItemProvider;
import eu.darkcube.system.provider.Provider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Obtain an instance with {@link VersionSupport#getVersion()}
 */
public interface Version {

    void init();

    CommandAPI commandApi();

    ItemProvider itemProvider();

    /**
     * For example 1_8_8 or 1_19_3
     *
     * @return this version's classifier
     */
    String getClassifier();

    Provider provider();

    interface CommandAPI {

        List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

        String getSpigotUnknownCommandMessage();

        PluginCommand registerLegacy(Plugin plugin, Command command);

        void unregister(String name);

        default void register() {
            register(null);
        }

        void register(CommandExecutor command);

        double[] getEntityBB(Entity entity);
    }
}

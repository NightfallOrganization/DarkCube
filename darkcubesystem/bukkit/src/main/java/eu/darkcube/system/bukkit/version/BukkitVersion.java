/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.version;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.bukkit.commandapi.deprecated.Command;
import eu.darkcube.system.bukkit.inventoryapi.item.ItemProvider;
import eu.darkcube.system.server.version.ServerVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Api public interface BukkitVersion extends ServerVersion {

    static BukkitVersion version() {
        return (BukkitVersion) ServerVersion.version();
    }

    /**
     * For example 1_8_8 or 1_19_3
     *
     * @return this version's classifier
     */
    @Api String classifier();

    @Api BukkitCommandAPIUtils commandApiUtils();

    @Api ItemProvider itemProvider();

    @Api interface BukkitCommandAPIUtils {
        @Api String unknownCommandMessage();

        @Api List<String> tabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

        @Api void register(eu.darkcube.system.bukkit.commandapi.Command command);

        @Api void unregister(eu.darkcube.system.bukkit.commandapi.Command command);

        @Api void unregister(String name);

        @Api PluginCommand registerLegacy(Plugin plugin, Command command);

        @Api double[] getEntityBB(Entity entity);
    }
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.Citybuild;
import eu.darkcube.system.citybuild.util.ResourcePackUtil;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class ResourcePackCommand extends CommandExecutor {
    public ResourcePackCommand(Citybuild citybuild) {
        super("metropolis", "resourcepack", new String[0], b -> b.then(Commands.literal("reload").executes(ctx -> {
            ctx.getSource().sendMessage(Component.text("Starting reload of ResourcePack"));
            citybuild.resourcePackUtil().reloadHash().thenRun(() -> {
                ctx.getSource().sendMessage(Component.text("Reload complete"));
            });
            return 0;
        })).then(Commands.literal("toggle").executes(ctx -> {
            Player player = ctx.getSource().asPlayer();
            boolean enabled = !player.getPersistentDataContainer().getOrDefault(ResourcePackUtil.KEY, PersistentDataType.BOOLEAN, true);
            player.getPersistentDataContainer().set(ResourcePackUtil.KEY, PersistentDataType.BOOLEAN, enabled);
            if (enabled) {
                citybuild.resourcePackUtil().sendResourcePack(player);
                player.sendMessage(net.kyori.adventure.text.Component.text("Enabled ResourcePack"));
            } else {
                citybuild.resourcePackUtil().sendResourcePackBlank(player);
                player.sendMessage(net.kyori.adventure.text.Component.text("Disabled ResourcePack"));
            }
            return 0;
        })));
    }
}
